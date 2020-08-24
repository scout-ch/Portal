package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.User;

import java.util.Optional;

public interface UserRepository extends BaseEntityRepository<User> {
    Optional<User> findByUsername(String email);

    Optional<User> findByMidataUserId(Long email);
}
