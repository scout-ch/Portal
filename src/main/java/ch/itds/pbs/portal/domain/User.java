package ch.itds.pbs.portal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Set;

@Table(name = "`user`")
@Entity
@EntityListeners({AuditingEntityListener.class})
@Getter
@Setter
public class User extends BaseEntity {

    @Email
    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    private boolean accountExpired;

    private boolean accountLocked;

    private boolean passwordExpired;

    private boolean enabled;

    private String firstName;

    private Long midataUserId;

    private String lastName;

    private String nickName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


}
