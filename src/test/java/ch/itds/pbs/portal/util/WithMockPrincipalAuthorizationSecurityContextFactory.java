package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.security.UserPrincipal;
import org.assertj.core.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WithMockPrincipalAuthorizationSecurityContextFactory implements WithSecurityContextFactory<WithMockPrincipalAuthorization> {
    @Override
    public SecurityContext createSecurityContext(WithMockPrincipalAuthorization authorization) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> authorities = Arrays.asList(authorization.roles())
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());

        UserPrincipal principal = new UserPrincipal(authorization.userId(), authorization.userId(), Long.toString(authorization.userId()),
                "", "", authorities, true, false, false, false, Locale.GERMAN);

        UsernamePasswordAuthenticationToken auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, "none", authorities);

        context.setAuthentication(auth);
        return context;
    }
}
