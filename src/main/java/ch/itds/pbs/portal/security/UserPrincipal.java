package ch.itds.pbs.portal.security;

import ch.itds.pbs.portal.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails, OAuth2User {


    private static final long serialVersionUID = 2007945438850451254L;

    private final Long id;
    private final String email;
    private final String nickName;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean enabled;
    private final boolean locked;
    private final boolean expired;
    private final boolean credentialsExpired;

    public UserPrincipal(Long id, String email, String nickName, String password, Collection<? extends GrantedAuthority> authorities,
                         boolean enabled, boolean locked, boolean expired, boolean credentialsExpired) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.locked = locked;
        this.expired = expired;
        this.credentialsExpired = credentialsExpired;
    }

    public static UserPrincipal create(User user) {
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getMail(),
                user.getNickName(),
                user.getPassword(),
                authorities,
                user.isEnabled(),
                user.isAccountLocked(),
                user.isAccountExpired(),
                user.isPasswordExpired()
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return getNickName();
    }

    public String getNickName() {
        return nickName;
    }

    public Long getId() {
        return id;
    }
}
