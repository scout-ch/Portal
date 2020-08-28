package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.FileMeta;
import ch.itds.pbs.portal.repo.FileMetaRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
@Slf4j
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
public class FileService {

    private final transient JdbcTemplate jdbcTemplate;
    private final transient FileMetaRepository fileMetaRepository;

    public FileService(JdbcTemplate jdbcTemplate, FileMetaRepository fileMetaRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileMetaRepository = fileMetaRepository;
    }

    @SuppressWarnings("PMD.AssignmentInOperand")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    // ignore transaction to allow created document to be committed immediately (otherwise the data update fails, because event with flush, it will be written at the end of the (external) transaction)
    public FileMeta upload(MultipartFile multipartFile) {

        if (multipartFile == null) {
            return null;
        }

        String filename = multipartFile.getOriginalFilename() == null ? "document" : multipartFile.getOriginalFilename();

        FileMeta fileMeta = new FileMeta();
        fileMeta.setContentType(multipartFile.getContentType());
        fileMeta.setContentSize(multipartFile.getSize());
        fileMeta.setName(filename);

        fileMeta = fileMetaRepository.saveAndFlush(fileMeta);

        final long newFileId = fileMeta.getId();

        DataSource ds = jdbcTemplate.getDataSource();
        if (ds != null) {
            try (Connection conn = ds.getConnection()) {

                conn.setAutoCommit(false);

                //Get the Large Object Manager to perform operations with
                LargeObjectManager lobj = conn.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();

                // Create a new large object
                long oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

                // Open the large object for writing
                LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

                // Now open the file
                InputStream fis = multipartFile.getInputStream();

                // Copy the data from the file to the large object
                byte[] buf = new byte[2048];
                int s;
                while ((s = fis.read(buf, 0, 2048)) > 0) {
                    obj.write(buf, 0, s);
                }

                // Close the large object
                obj.close();

                // Now insert the row into file
                PreparedStatement ps = conn.prepareStatement("UPDATE file_meta SET data = ?, version = version + 1 , last_updated = current_timestamp WHERE id = ?");
                ps.setLong(1, oid);
                ps.setLong(2, newFileId);
                int updated = ps.executeUpdate();
                ps.close();
                fis.close();

                // Finally, commit the transaction.
                conn.commit();

                if (updated <= 0) {
                    throw new IOException("document not found for update with id = " + newFileId);
                }
            } catch (SQLException | IOException e) {

                if (log.isErrorEnabled()) {
                    log.error("failed to save image data: {}", e.getMessage());
                }
                fileMetaRepository.delete(fileMeta);
                return null;
            }
        }

        fileMeta = fileMetaRepository.findById(fileMeta.getId()).orElseThrow(() -> new EntityNotFoundException("FileMeta#" + newFileId));

        return fileMeta;

    }

    public void sendFileContent(long fileId, OutputStream outputStream) {
        DataSource ds = jdbcTemplate.getDataSource();
        if (ds != null) {
            try (Connection conn = ds.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT data FROM file_meta WHERE id = ?")) {
                // All LargeObject API calls must be within a transaction block
                conn.setAutoCommit(false);

                // Get the Large Object Manager to perform operations with
                LargeObjectManager lobj = conn.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();

                ps.setLong(1, fileId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // Open the large object for reading
                        long oid = rs.getLong(1);
                        if (oid > 0) {
                            LargeObject obj = lobj.open(oid, LargeObjectManager.READ);

                            // Read the data
                            IOUtils.copy(obj.getInputStream(), outputStream);

                            // Close the object
                            obj.close();
                        }
                    }
                }

                // Finally, commit the transaction.
                conn.commit();
            } catch (SQLException | IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("unable to get data for document {}: {}", fileId, e.getMessage());
                }
            }
        }
    }

    public void delete(FileMeta file) {
        fileMetaRepository.delete(file);
    }

    public Optional<FileMeta> getMasterTileFile(long masterTileId) {
        return fileMetaRepository.findAsMasterTileImage(masterTileId);
    }
}
