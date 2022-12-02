package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.FileMeta;
import ch.itds.pbs.portal.service.FileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/tile")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class TileFileController {

    private final transient FileService fileService;

    public TileFileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("/masterFile/{tileId}/{tileVersion}/{fileName}")
    public void show(
            @PathVariable long tileId,
            @PathVariable int tileVersion,
            @PathVariable String fileName,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Optional<FileMeta> file = fileService.getMasterTileFile(tileId);

        if (!file.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }


        FileMeta fileInstance = file.get();

        String etag = fileInstance.getId() + "-" + fileInstance.getVersion();

        if (etag.equals(request.getHeader("If-None-Match"))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        response.setHeader("ETag", etag);
        response.setHeader("Cache-Control", "max-age=533280");

        response.setContentType(fileInstance.getContentType());
        response.setContentLength((int) fileInstance.getContentSize());
        response.setHeader("filename", fileInstance.getName());
        response.setHeader("Content-Disposition", "inline; filename=\"" + (fileInstance.getName()) + "\"");

        fileService.sendFileContent(fileInstance.getId(), response.getOutputStream());


    }

}
