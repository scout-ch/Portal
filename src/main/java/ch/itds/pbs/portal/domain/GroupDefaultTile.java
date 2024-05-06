package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class GroupDefaultTile extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private MasterTile masterTile;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private MidataGroup group;

    private int position = -1;

}
