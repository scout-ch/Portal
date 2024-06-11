package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToMany;
import java.util.Set;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class Role extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private Set<User> users;

}
