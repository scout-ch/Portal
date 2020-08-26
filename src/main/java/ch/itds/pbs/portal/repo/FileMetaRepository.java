package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.FileMeta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileMetaRepository extends BaseEntityRepository<FileMeta> {

    @Query("SELECT mt.image FROM MasterTile mt WHERE mt.id = :masterTileId")
    Optional<FileMeta> findAsMasterTileImage(@Param("masterTileId") long masterTileId);
}
