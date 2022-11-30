package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.MidataGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends BaseEntityRepository<Category> {

    @Query(value = "SELECT c.* FROM category c WHERE c.name_de = :name LIMIT 1", nativeQuery = true)
    Optional<Category> findByNameDe(@Param("name") String name);

    List<Category> findAllByMidataGroupOnly(MidataGroup group);

    @Query(value = "SELECT c FROM Category c WHERE c.midataGroupOnly IS NULL OR c.midataGroupOnly = :midataGroup")
    List<Category> findAllForGroup(MidataGroup midataGroup);
}
