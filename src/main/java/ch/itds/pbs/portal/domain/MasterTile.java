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
public class MasterTile extends BaseEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "title_de")),
            @AttributeOverride(name = "fr", column = @Column(name = "title_fr")),
            @AttributeOverride(name = "it", column = @Column(name = "title_it")),
            @AttributeOverride(name = "en", column = @Column(name = "title_en"))
    })
    private LocalizedString title = new LocalizedString();

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color titleColor = Color.DEFAULT;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "content_de")),
            @AttributeOverride(name = "fr", column = @Column(name = "content_fr")),
            @AttributeOverride(name = "it", column = @Column(name = "content_it")),
            @AttributeOverride(name = "en", column = @Column(name = "content_en"))
    })
    private LocalizedString content = new LocalizedString();

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color contentColor = Color.DEFAULT;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color backgroundColor = Color.DEFAULT;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "url_de")),
            @AttributeOverride(name = "fr", column = @Column(name = "url_fr")),
            @AttributeOverride(name = "it", column = @Column(name = "url_it")),
            @AttributeOverride(name = "en", column = @Column(name = "url_en"))
    })
    private LocalizedString url = new LocalizedString();

    @ManyToOne
    @JoinColumn
    private FileMeta image;

    @ManyToOne(optional = false)
    @JoinColumn
    private Category category;

    private int position = -1;

    private String apiKey;

}
