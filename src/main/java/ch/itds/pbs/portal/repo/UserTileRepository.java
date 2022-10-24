package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface UserTileRepository extends BaseEntityRepository<UserTile> {

    List<UserTile> findAllByUser(User user);

    @Query("SELECT ut FROM UserTile ut LEFT JOIN FETCH ut.masterTile mt LEFT JOIN FETCH ut.messages LEFT JOIN FETCH mt.category c LEFT JOIN FETCH mt.image WHERE ut.user.id = :userId AND mt.enabled = TRUE ORDER BY ut.position")
    Stream<UserTile> findAllEnabledWithFetchForUserId(@Param("userId") Long id);

    @Query("SELECT ut FROM UserTile ut LEFT JOIN FETCH ut.user u WHERE ut.masterTile.id = :masterTileId AND u.midataUserId IN :limitToUserIds")
    List<UserTile> findAllByMasterTileIdAndUserMidataIdWithUser(@Param("masterTileId") Long masterTileId, @Param("limitToUserIds") List<Long> limitToUserIds);
}
