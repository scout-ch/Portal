package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.GroupDefaultTile;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.MidataGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupDefaultTileRepository extends BaseEntityRepository<GroupDefaultTile> {

    @Query("SELECT gdt FROM GroupDefaultTile gdt LEFT JOIN FETCH gdt.masterTile mt LEFT JOIN FETCH mt.category c LEFT JOIN FETCH mt.image WHERE gdt.group.id = :groupId AND mt.enabled = TRUE ORDER BY gdt.position")
    List<GroupDefaultTile> findAllEnabledWithFetchForUserId(Long groupId);

    List<GroupDefaultTile> findAllByGroup(MidataGroup midataGroup);

    List<GroupDefaultTile> findAllByGroupAndMasterTile(MidataGroup midataGroup, MasterTile masterTile);

    Optional<GroupDefaultTile> findByIdAndGroup(long id, MidataGroup midataGroup);
}
