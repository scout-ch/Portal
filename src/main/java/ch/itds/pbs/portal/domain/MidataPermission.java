package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class MidataPermission extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private MidataRole role;

    @Column(nullable = false)
    private String permission;


}
