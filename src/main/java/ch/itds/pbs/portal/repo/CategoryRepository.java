package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends BaseEntityRepository<Category> {
    Optional<Category> findByName(String name);
}
