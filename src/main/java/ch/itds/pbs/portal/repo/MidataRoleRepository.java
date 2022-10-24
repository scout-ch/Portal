package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.domain.MidataRole;

public interface MidataRoleRepository extends BaseEntityRepository<MidataRole> {

    MidataRole findByGroupAndName(MidataGroup group, String roleName);
}
