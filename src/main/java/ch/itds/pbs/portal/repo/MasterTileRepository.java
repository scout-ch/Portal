package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MasterTile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MasterTileRepository extends BaseEntityRepository<MasterTile> {

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title_de = :title LIMIT 1", nativeQuery = true)
    Optional<MasterTile> findFirstByTitleDe(@Param("title") String title);

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title_en = :title LIMIT 1", nativeQuery = true)
    Optional<MasterTile> findFirstByTitleEn(@Param("title") String title);

    @Query("SELECT mt FROM MasterTile mt LEFT JOIN FETCH mt.category ORDER BY mt.position ASC")
    List<MasterTile> findAllWithCategory();

    @Query("SELECT mt FROM MasterTile mt WHERE mt.enabled = TRUE AND mt.apiKey = :apiKey")
    MasterTile findEnabledByApiKey(@Param("apiKey") String apiKey);
}
