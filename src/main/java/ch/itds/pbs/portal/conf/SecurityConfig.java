package ch.itds.pbs.portal.conf;


import ch.itds.pbs.portal.filter.TileTokenAuthenticationFilter;
import ch.itds.pbs.portal.security.LocaleSettingAuthenticationSuccessHandler;
import ch.itds.pbs.portal.security.oauth.MidataOAuth2UserService;
import ch.itds.pbs.portal.security.tile.TileAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final transient UserDetailsService userDetailsService;
    private final transient MidataOAuth2UserService midataOAuth2UserService;
    private final transient LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler;
    private final transient TileAuthenticationProvider tileAuthenticationProvider;

    public SecurityConfig(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService, MidataOAuth2UserService midataOAuth2UserService, LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler, TileAuthenticationProvider tileAuthenticationProvider) {
        super();
        this.userDetailsService = userDetailsService;

        this.midataOAuth2UserService = midataOAuth2UserService;
        this.localeSettingAuthenticationSuccessHandler = localeSettingAuthenticationSuccessHandler;
        this.tileAuthenticationProvider = tileAuthenticationProvider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf()
                .ignoringAntMatchers("/api/v1/**")
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .formLogin()
                .loginPage("/auth/login")
                .successHandler(localeSettingAuthenticationSuccessHandler)
                .permitAll()
                .and()
                .httpBasic()
                .disable()
                .authorizeRequests()
                .mvcMatchers("/admin", "/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/error",
                        "/assets/**"
                ).permitAll()
                .antMatchers("/auth/**", "/oauth2/**", "/logoutMidata/**").permitAll()
                .antMatchers("/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).access("hasIpAddress('127.0.0.1') or hasRole('ADMIN')")
                .antMatchers("/api/v1/**").hasRole("TILE")
                .and()
                .authenticationProvider(tileAuthenticationProvider)
                .addFilterBefore(new TileTokenAuthenticationFilter(), BasicAuthenticationFilter.class)
                .oauth2Login()
                .successHandler(localeSettingAuthenticationSuccessHandler)
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(midataOAuth2UserService)
                .and()
                .and();

    }
}