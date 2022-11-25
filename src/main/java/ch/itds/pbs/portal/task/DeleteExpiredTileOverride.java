package ch.itds.pbs.portal.task;

import ch.itds.pbs.portal.repo.TileOverrideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DeleteExpiredTileOverride {

    private final TileOverrideRepository tileOverrideRepository;

    public DeleteExpiredTileOverride(TileOverrideRepository tileOverrideRepository) {
        this.tileOverrideRepository = tileOverrideRepository;
    }

    @Scheduled(initialDelay = 60_000, fixedDelay = 60_000 * 60)
    @Transactional
    public void deleteExpired() {

        tileOverrideRepository.deleteAll(tileOverrideRepository.findAllByValidUntilBefore(LocalDateTime.now()));

    }

}
