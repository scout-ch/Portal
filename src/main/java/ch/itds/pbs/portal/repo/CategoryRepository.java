package ch.itds.pbs.portal.repo;

import ch.itds.pbs.portal.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends BaseEntityRepository<Category> {

    @Query(value = "SELECT c.* FROM category c WHERE c.name_de = :name LIMIT 1", nativeQuery = true)
    Optional<Category> findByNameDe(@Param("name") String name);
}
