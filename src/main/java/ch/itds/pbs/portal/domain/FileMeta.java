package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class FileMeta extends BaseEntity {

    private String name;
    private String contentType;
    private long contentSize;

}
