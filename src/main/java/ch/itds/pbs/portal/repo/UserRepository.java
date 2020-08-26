package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends BaseEntityRepository<User> {
    Optional<User> findByUsername(String email);

    Optional<User> findByMidataUserId(Long email);

    @Modifying
    @Query("UPDATE User SET language = :language WHERE id = :userId")
    void setLanguage(@Param("userId") Long userId, @Param("language") Language language);
}
