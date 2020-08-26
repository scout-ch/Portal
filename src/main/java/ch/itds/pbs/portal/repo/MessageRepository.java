package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends BaseEntityRepository<Message> {

    @Query("SELECT m FROM Message m JOIN FETCH m.userTile ut LEFT JOIN FETCH ut.masterTile mt  WHERE ut.user.id = :userId ORDER BY m.dateCreated DESC")
    List<Message> findAllCompleteByUserId(@Param("userId") long userId);

    @Query("SELECT count(m) FROM Message m WHERE m.read = false AND m.userTile.user.id = :userId")
    int countUnreadMessages(@Param("userId") long userId);

    @Modifying
    @Query(value = "UPDATE message  SET read = TRUE FROM user_tile  WHERE message.id = :messageId AND message.user_tile_id = user_tile.id AND user_tile.user_id = :userId", nativeQuery = true)
    int setRead(@Param("userId") long userId, @Param("messageId") long messageId);

    @Modifying
    @Query(value = "DELETE FROM message USING user_tile WHERE message.id = :messageId AND user_tile.id = message.user_tile_id AND user_tile.user_id = :userId", nativeQuery = true)
    int delete(@Param("userId") long userId, @Param("messageId") long messageId);
}
