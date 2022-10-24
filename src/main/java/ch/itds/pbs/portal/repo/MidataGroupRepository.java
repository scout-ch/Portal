package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MidataGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MidataGroupRepository extends BaseEntityRepository<MidataGroup> {

    MidataGroup findByMidataId(Integer groupId);

    @Query("SELECT DISTINCT mg FROM MidataPermission mp LEFT JOIN mp.role mr LEFT JOIN mr.group mg WHERE mp.user.id = :id AND ( mp.permission = 'admin' OR mp.permission = 'group_and_below_full' ) ORDER BY mg.name")
    List<MidataGroup> findWithAdminPermission(Long id);
}
