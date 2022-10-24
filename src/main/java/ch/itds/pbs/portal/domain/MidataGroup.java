package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class MidataGroup extends BaseEntity {

    @Column(nullable = false)
    private Integer midataId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<MidataRole> roles;

}
