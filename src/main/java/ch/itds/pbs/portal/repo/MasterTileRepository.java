package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.MidataGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MasterTileRepository extends BaseEntityRepository<MasterTile> {

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title_de = :title LIMIT 1", nativeQuery = true)
    Optional<MasterTile> findFirstByTitleDe(@Param("title") String title);

    @Query(value = "SELECT mt.* FROM master_tile mt WHERE mt.title_en = :title LIMIT 1", nativeQuery = true)
    Optional<MasterTile> findFirstByTitleEn(@Param("title") String title);

    @Query("SELECT mt FROM MasterTile mt LEFT JOIN FETCH mt.category ORDER BY mt.position ASC")
    List<MasterTile> findAllWithCategory();

    @Query("SELECT mt FROM MasterTile mt WHERE mt.enabled = TRUE AND mt.apiKey = :apiKey")
    MasterTile findEnabledByApiKey(@Param("apiKey") String apiKey);

    @Query("SELECT mt FROM MasterTile mt LEFT JOIN FETCH mt.category WHERE mt.midataGroupOnly = :midataGroup ORDER BY mt.position ASC")
    List<MasterTile> findAllByGroupWithCategory(MidataGroup midataGroup);

    Optional<MasterTile> findByIdAndMidataGroupOnly(long id, MidataGroup midataGroup);

    List<MasterTile> findAllByMidataGroupOnly(MidataGroup midataGroup);

    @Query("SELECT DISTINCT mt FROM MidataPermission p LEFT JOIN p.role r LEFT JOIN r.group g LEFT JOIN g.tiles mt LEFT JOIN FETCH mt.category c LEFT JOIN FETCH mt.image WHERE p.user.id = :userId AND mt.enabled = TRUE ORDER BY mt.position")
    Stream<MasterTile> findAllEnabledWithFetchForUserByGroups(Long userId);

    @Query("SELECT DISTINCT mt FROM MidataPermission p LEFT JOIN p.role r LEFT JOIN r.group g LEFT JOIN g.tiles mt LEFT JOIN FETCH mt.category c LEFT JOIN FETCH mt.image WHERE p.user.id = :userId AND mt.enabled = TRUE AND mt.id = :masterTileId")
    Optional<MasterTile> findEnabledWithFetchForUserByIdAndGroups(Long masterTileId, Long userId);
}
