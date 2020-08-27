package ch.itds.pbs.portal.conf;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenAPI30Config {

    final private transient BuildProperties buildProperties;

    public OpenAPI30Config(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes("Tile API Key",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .name("X-Tile-Authorization")
                                )
                )
                .addSecurityItem(new SecurityRequirement().addList("Tile API Key"))
                .info(getApiInfo());
    }

    private Info getApiInfo() {
        return new Info()
                .title("PBS Portal REST API")
                .description("")
                .version(buildProperties.getVersion())
                .contact(new Contact().name("Pfadibewegung Schweiz").url("https://www.pfadi.swiss/"));
    }
}