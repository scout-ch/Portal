package ch.itds.pbs.portal.conf;


import ch.itds.pbs.portal.filter.TileTokenAuthenticationFilter;
import ch.itds.pbs.portal.security.tile.TileAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityApiConfig {

    private final transient TileAuthenticationManager tileAuthenticationManager;
    private final transient Environment environment;

    public SecurityApiConfig(Environment environment, TileAuthenticationManager tileAuthenticationManager) {
        super();

        this.environment = environment;
        this.tileAuthenticationManager = tileAuthenticationManager;
    }

    @Bean
    @Order(0)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/api/v1/**"))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/v1/**").hasRole("TILE")
                )
                .authenticationProvider(new PreAuthenticatedAuthenticationProvider())
                .addFilterBefore(new TileTokenAuthenticationFilter(tileAuthenticationManager), BasicAuthenticationFilter.class);


        if (environment.acceptsProfiles(Profiles.of("development"))) {
            http.headers(headersConfigurer -> headersConfigurer.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable));
        }

        return http.build();

    }
}