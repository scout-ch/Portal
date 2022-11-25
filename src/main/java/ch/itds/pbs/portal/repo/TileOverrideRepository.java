package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.TileOverride;

import java.time.LocalDateTime;
import java.util.List;

public interface TileOverrideRepository extends BaseEntityRepository<TileOverride> {

    List<TileOverride> findAllByValidUntilBefore(LocalDateTime timestamp);
}
