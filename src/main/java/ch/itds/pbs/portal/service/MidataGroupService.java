package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MidataGroupService {

    private final MidataGroupRepository midataGroupRepository;

    private final CategoryRepository categoryRepository;

    public MidataGroupService(MidataGroupRepository midataGroupRepository, CategoryRepository categoryRepository) {
        this.midataGroupRepository = midataGroupRepository;
        this.categoryRepository = categoryRepository;
    }

    public MidataGroup findByIdAndEnsureAdmin(long midataGroupId, long userId) {
        return midataGroupRepository.findWithAdminPermission(midataGroupId, userId).orElseThrow(EntityNotFoundException::new);
    }

    public Category getCategory(MidataGroup midataGroup) {
        List<Category> categoryList = categoryRepository.findAllByMidataGroupOnly(midataGroup);
        if (categoryList.size() == 1) {
            return categoryList.get(0);
        } else {
            return null;
        }
    }
}
