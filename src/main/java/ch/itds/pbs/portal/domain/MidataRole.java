package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class MidataRole extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private MidataGroup group;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String clazz;

}
