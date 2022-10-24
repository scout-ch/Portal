package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MidataPermission;
import ch.itds.pbs.portal.domain.MidataRole;
import ch.itds.pbs.portal.domain.User;

import java.util.List;

public interface MidataPermissionRepository extends BaseEntityRepository<MidataPermission> {

    MidataPermission findByUserAndRoleAndPermission(User user, MidataRole role, String p);

    List<MidataPermission> findByUser(User user);
}
