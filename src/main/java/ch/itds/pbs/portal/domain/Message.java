package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class Message extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private UserTile userTile;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String url;

    private boolean read = false;

}
