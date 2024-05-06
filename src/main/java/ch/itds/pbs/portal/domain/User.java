package ch.itds.pbs.portal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
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

    @Enumerated(EnumType.STRING)
    private Language language = Language.DE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    private MidataGroup primaryMidataGroup;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<MidataPermission> midataPermissions;

    @JdbcTypeCode(Types.ARRAY)
    private Integer[] midataGroupHierarchy = {};

}
