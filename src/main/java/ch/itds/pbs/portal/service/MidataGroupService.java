package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class MidataGroupService {

    private final MidataGroupRepository midataGroupRepository;

    public MidataGroupService(MidataGroupRepository midataGroupRepository) {
        this.midataGroupRepository = midataGroupRepository;
    }

    public MidataGroup findByIdAndEnsureAdmin(long midataGroupId, long userId) {
        return midataGroupRepository.findWithAdminPermission(midataGroupId, userId).orElseThrow(EntityNotFoundException::new);
    }
}
