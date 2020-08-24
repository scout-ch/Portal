package ch.itds.pbs.portal.conf;

import ch.itds.taglib.asset.pipeline.thymeleaf.AssetDialect;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

    private final transient MessageSource messageSource;

    public ThymeleafConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public AssetDialect assetDialect() {
        return new AssetDialect();
    }

}
