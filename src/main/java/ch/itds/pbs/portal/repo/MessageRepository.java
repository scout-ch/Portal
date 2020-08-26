package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends BaseEntityRepository<Message> {

    @Query("SELECT m FROM Message m JOIN FETCH m.userTile ut LEFT JOIN FETCH ut.masterTile mt  WHERE ut.user.id = :userId ORDER BY m.dateCreated DESC")
    List<Message> findAllCompleteByUserId(@Param("userId") Long userId);

    @Query("SELECT count(m) FROM Message m WHERE m.read = false AND m.userTile.user.id = :userId")
    int countUnreadMessages(@Param("userId") Long userId);
}
