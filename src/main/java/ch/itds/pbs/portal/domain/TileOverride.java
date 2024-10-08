package ch.itds.pbs.portal.domain;

import ch.itds.pbs.portal.conf.WebConfig;
import ch.itds.pbs.portal.util.validation.OneLocalizationRequired;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
@OneLocalizationRequired(fields = {"title", "content"})
public class TileOverride extends BaseEntity {

    @Column
    @DateTimeFormat(pattern = WebConfig.DEFAULT_LOCAL_DATE_TIME_PATTERN)
    @NotNull
    LocalDateTime validUntil;
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

    public List<Language> getAvailableLanguages() {
        List<Language> languages = new ArrayList<>();
        for (Language l : Language.values()) {
            if (!Strings.isEmpty(title.getOrDefault(l, null))
                    && !Strings.isEmpty(content.getOrDefault(l, null))) {
                languages.add(l);
            }
        }
        return languages;
    }

}
