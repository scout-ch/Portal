package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MasterTile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MasterTileRepository extends BaseEntityRepository<MasterTile> {

    @Query("SELECT mt FROM UserTile ut LEFT JOIN ut.masterTile mt WHERE ut.user.id = :userId ORDER BY mt.position")
    List<MasterTile> findAllForUserId(@Param("userId") Long id);

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title -> 'DE' = :name", nativeQuery = true)
    Optional<MasterTile> findByNameDe(@Param("name") String name);
}
