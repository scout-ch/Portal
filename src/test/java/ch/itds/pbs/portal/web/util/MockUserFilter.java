package ch.itds.pbs.portal.web.util;

import ch.itds.pbs.portal.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.support.WebTestUtils;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MockUserFilter extends GenericFilterBean {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private SecurityContext securityContext;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (securityContext != null) {
            SecurityContextRepository securityContextRepository = WebTestUtils.getSecurityContextRepository(request);

            HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
            securityContextRepository.loadContext(requestResponseHolder);

            request = requestResponseHolder.getRequest();
            response = requestResponseHolder.getResponse();
            securityContextRepository.saveContext(securityContext, request, response);

            securityContext = null;
        }
        chain.doFilter(request, response);
    }

    /**
     * Fake authentication
     *
     * @param username the MiData UserID
     */
    public void authenticateNextRequestAs(String username) {
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

    }
}
