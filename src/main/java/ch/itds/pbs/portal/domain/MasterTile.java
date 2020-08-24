package ch.itds.pbs.portal.domain;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
public class MasterTile extends BaseEntity {

    @Type(type = "hstore")
    private Map<Language, String> title = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color titleColor = Color.DEFAULT;

    @Type(type = "hstore")
    private Map<Language, String> content = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color contentColor = Color.DEFAULT;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Color backgroundColor = Color.DEFAULT;

    @Type(type = "hstore")
    private Map<Language, String> url = new HashMap<>();

    @ManyToOne
    @JoinColumn
    private FileMeta image;

    @ManyToOne(optional = false)
    @JoinColumn
    private Category category;

    private int position = -1;

    private String apiKey;

}
