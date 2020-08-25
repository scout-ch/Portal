package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Role;

import java.util.Optional;

public interface RoleRepository extends BaseEntityRepository<Role> {
    Optional<Role> findByName(String admin);
}
