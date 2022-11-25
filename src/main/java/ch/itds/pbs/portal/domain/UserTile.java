package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class UserTile extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private MasterTile masterTile;

    @ManyToOne
    @JoinColumn
    private TileOverride override;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private User user;

    @OneToMany(mappedBy = "userTile", cascade = CascadeType.REMOVE)
    private Set<Message> messages;

    private int position = -1;

}
