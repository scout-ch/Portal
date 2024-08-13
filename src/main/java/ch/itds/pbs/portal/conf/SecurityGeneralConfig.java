package ch.itds.pbs.portal.conf;


import ch.itds.pbs.portal.security.LocaleSettingAuthenticationSuccessHandler;
import ch.itds.pbs.portal.security.oauth.MidataOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityGeneralConfig {

    private final transient MidataOAuth2UserService midataOAuth2UserService;
    private final transient LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler;
    private final transient Environment environment;

    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;

    public SecurityGeneralConfig(Environment environment, MidataOAuth2UserService midataOAuth2UserService, LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler) {
        super();

        this.environment = environment;
        this.midataOAuth2UserService = midataOAuth2UserService;
        this.localeSettingAuthenticationSuccessHandler = localeSettingAuthenticationSuccessHandler;
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
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(EndpointRequest.toAnyEndpoint())
                        .access((new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasRole('ADMIN')")))
                        .requestMatchers("/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/share/tile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/catalog").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/message", "/message/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/userTile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/tile/masterFile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/midataGroup", "/midataGroup/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/error",
                                "/assets/**",
                                "/static/**"
                        ).permitAll()
                        .requestMatchers("/login/**", "/auth/**", "/oauth2/**", "/logout").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                )
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(oAuth2LoginConfigurer ->
                        oAuth2LoginConfigurer
                                .loginPage("/auth/login")
                                .redirectionEndpoint(config -> config.baseUri("/oauth2/callback/*"))
                                .authorizationEndpoint(config -> config.baseUri("/oauth2/authorize"))
                                .successHandler(localeSettingAuthenticationSuccessHandler)
                                .userInfoEndpoint(config -> config.userService(midataOAuth2UserService))
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutUrl("/logout")
                                .logoutSuccessHandler(logoutSuccessHandler)
                );

        if (environment.acceptsProfiles(Profiles.of("development"))) {
            http.headers(headersConfigurer -> headersConfigurer.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable));
        }

        return http.build();

    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository repository) {

        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(repository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logout-success");

        return oidcLogoutSuccessHandler;
    }
}