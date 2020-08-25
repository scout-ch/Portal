package ch.itds.pbs.portal.conf;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.MasterTile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "bootstrap.data")
@Getter
@Setter
public class DevelopmentBootstrapData {

    private List<Category> categories = new ArrayList<>();
    private List<MasterTile> masterTiles = new ArrayList<>();

}
