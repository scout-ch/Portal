package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface UserTileRepository extends BaseEntityRepository<UserTile> {

    List<UserTile> findAllByUser(User user);

    @Query("SELECT ut FROM UserTile ut LEFT JOIN FETCH ut.masterTile mt LEFT JOIN FETCH ut.messages LEFT JOIN FETCH mt.category c LEFT JOIN FETCH mt.image WHERE ut.user.id = :userId ORDER BY mt.position")
    Stream<UserTile> findAllWithFetchForUserId(@Param("userId") Long id);

}
