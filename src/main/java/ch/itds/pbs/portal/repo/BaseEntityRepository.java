package ch.itds.pbs.portal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public
interface BaseEntityRepository<E> extends JpaRepository<E, Long> {
}
