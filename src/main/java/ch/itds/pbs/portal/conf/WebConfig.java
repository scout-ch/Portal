package ch.itds.pbs.portal.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String DEFAULT_LOCAL_DATE_PATTERN = "dd.MM.yyyy";
    public static final String DEFAULT_LOCAL_DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final String DEFAULT_AMOUNT_PATTERN = "0.000";
    public static final String DEFAULT_PRICE_PATTERN = "0.00";
    private final static long MAX_AGE_SECS = 3600;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag("de-CH"));
        return slr;
    }

    @Profile("development")
    @Bean("messageSourceCacheDuration")
    Integer developmentMessageSourceCache() {
        return 5;
    }

    @Profile("!development")
    @Bean("messageSourceCacheDuration")
    Integer nonDevelopmentMessageSourceCache() {
        return -1;
    }

    @Bean
    public MessageSource messageSource(@Qualifier("messageSourceCacheDuration") Integer messageSourceCacheDuration) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:/i18n/messages"
        );
        messageSource.setDefaultEncoding("UTF-8");
        //messageSource.setUseCodeAsDefaultMessage(true); // must be default=false to /beefOrder/create : options fallback
        messageSource.setCacheSeconds(messageSourceCacheDuration);
        return messageSource;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

}
