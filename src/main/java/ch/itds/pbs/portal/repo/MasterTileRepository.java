package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MasterTile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MasterTileRepository extends BaseEntityRepository<MasterTile> {

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title_de = :name", nativeQuery = true)
    Optional<MasterTile> findByNameDe(@Param("name") String name);
}
