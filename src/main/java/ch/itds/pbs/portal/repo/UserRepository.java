package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends BaseEntityRepository<User> {
    Optional<User> findByUsername(String midataUserId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.primaryMidataGroup LEFT JOIN FETCH u.midataPermissions WHERE u.username = :midataUserId")
    Optional<User> findByUsernameWithPrimaryMidataGroupAndMidataPermissions(String midataUserId);

    Optional<User> findByMidataUserId(Long email);

    @Modifying
    @Query("UPDATE User SET language = :language WHERE id = :userId")
    void setLanguage(@Param("userId") Long userId, @Param("language") Language language);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
}
