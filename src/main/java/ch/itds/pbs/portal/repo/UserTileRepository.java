package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;

import java.util.List;

public interface UserTileRepository extends BaseEntityRepository<UserTile> {
    List<UserTile> findAllByUser(User user);
}
