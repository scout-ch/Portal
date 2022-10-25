package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MidataGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MidataGroupRepository extends BaseEntityRepository<MidataGroup> {

    MidataGroup findByMidataId(Integer groupId);

    @Query("SELECT DISTINCT mg FROM MidataPermission mp LEFT JOIN mp.role mr LEFT JOIN mr.group mg WHERE mp.user.id = :userId AND ( mp.permission = 'admin' OR mp.permission = 'group_and_below_full' ) ORDER BY mg.name")
    List<MidataGroup> findAllWithAdminPermission(long userId);

    @Query("SELECT DISTINCT mg FROM MidataPermission mp LEFT JOIN mp.role mr LEFT JOIN mr.group mg WHERE mg.id = :groupId AND mp.user.id = :userId AND ( mp.permission = 'admin' OR mp.permission = 'group_and_below_full' ) ORDER BY mg.name")
    Optional<MidataGroup> findWithAdminPermission(long groupId, long userId);

}
