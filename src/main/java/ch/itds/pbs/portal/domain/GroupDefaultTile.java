package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
