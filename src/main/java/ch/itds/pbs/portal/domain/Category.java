package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class Category extends BaseEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "name_de")),
            @AttributeOverride(name = "fr", column = @Column(name = "name_fr")),
            @AttributeOverride(name = "it", column = @Column(name = "name_it")),
            @AttributeOverride(name = "en", column = @Column(name = "name_en"))
    })
    private LocalizedString name = new LocalizedString();

    @OneToMany
    private Set<MasterTile> tiles;

    /**
     * The category is only available for members of this group
     */
    @ManyToOne
    @JoinColumn
    private MidataGroup midataGroupOnly;

}
