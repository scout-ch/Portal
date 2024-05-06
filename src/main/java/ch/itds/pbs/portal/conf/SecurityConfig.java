package ch.itds.pbs.portal.conf;


import ch.itds.pbs.portal.filter.TileTokenAuthenticationFilter;
import ch.itds.pbs.portal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecurityConfig {

    private final transient Environment environment;
    private final transient UserService userService;
    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;

    public SecurityConfig(Environment environment, UserService userService) {
        super();

        this.environment = environment;
        this.userService = userService;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisherRegistration(HttpSessionEventPublisher httpSessionEventPublisher) {
        return new ServletListenerRegistrationBean<>(httpSessionEventPublisher);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry registry) {
        return new RegisterSessionAuthenticationStrategy(registry);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(EndpointRequest.toAnyEndpoint())
                        .access((new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasRole('ADMIN')")))
                        .requestMatchers("/midataGroup", "/midataGroup/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/error",
                                "/assets/**"
                        ).permitAll()
                        .requestMatchers("/auth/**", "/oauth2/**", "/logoutMidata/**").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/**").hasRole("TILE")
                )
                .authenticationProvider(new PreAuthenticatedAuthenticationProvider())
                .addFilterBefore(new TileTokenAuthenticationFilter(), BasicAuthenticationFilter.class)
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(oAuth2LoginConfigurer ->
                        oAuth2LoginConfigurer
                                .loginPage("/auth/login")
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(userAuthoritiesMapper()))
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutUrl("/logoutMidata/**")
                                .logoutSuccessHandler(logoutSuccessHandler)
                );

        if (environment.acceptsProfiles(Profiles.of("development"))) {
            http.headers(headersConfigurer -> headersConfigurer.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable));
        }

        return http.build();

    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {

                if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                    List<String> roleNames = userService.extractRoleNames(userInfo);
                    List<SimpleGrantedAuthority> groupAuthorities = new ArrayList<>(roleNames.size());
                    for (String name : roleNames) {
                        groupAuthorities.add(new SimpleGrantedAuthority("ROLE_" + name));
                    }
                    mappedAuthorities.addAll(groupAuthorities);
                    userService.updateRoles(userInfo.getPreferredUsername(), roleNames);
                }
            });

            return mappedAuthorities;
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository repository) {

        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(repository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logout-success");

        return oidcLogoutSuccessHandler;
    }
}