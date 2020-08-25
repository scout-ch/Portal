package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.conf.DevelopmentBootstrapData;
import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.Role;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.RoleRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.TileService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DevelopmentInitializer {

    private final transient Environment environment;
    private final transient TileService tileService;
    private final transient UserRepository userRepository;
    private final transient RoleRepository roleRepository;
    private final transient MasterTileRepository masterTileRepository;
    private final transient CategoryRepository categoryRepository;
    private final transient DevelopmentBootstrapData developmentBootstrapData;

    public DevelopmentInitializer(Environment environment, TileService tileService, UserRepository userRepository, RoleRepository roleRepository, MasterTileRepository masterTileRepository, CategoryRepository categoryRepository, DevelopmentBootstrapData developmentBootstrapData) {
        this.environment = environment;
        this.tileService = tileService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.masterTileRepository = masterTileRepository;
        this.categoryRepository = categoryRepository;
        this.developmentBootstrapData = developmentBootstrapData;
    }

    @PostConstruct
    public void init() {
        if (environment.acceptsProfiles(Profiles.of("development"))) {

            if (developmentBootstrapData.getCategories() != null) {
                for (Category c : developmentBootstrapData.getCategories()) {
                    Category savedCategory = categoryRepository.findByName(c.getName()).orElseGet(() -> new Category());
                    BeanUtils.copyProperties(c, savedCategory, "id", "version", "dateCreated", "lastUpdated", "tiles");
                    categoryRepository.save(savedCategory);
                }
            }
            if (developmentBootstrapData.getMasterTiles() != null) {
                for (MasterTile mt : developmentBootstrapData.getMasterTiles()) {
                    mt.setCategory(categoryRepository.findByName(mt.getCategory().getName()).orElse(null));
                    MasterTile savedMasterTile = masterTileRepository.findFirstByTitleDe(mt.getTitle().getDe()).orElseGet(() -> new MasterTile());
                    BeanUtils.copyProperties(mt, savedMasterTile, "id", "version", "dateCreated", "lastUpdated", "image");
                    masterTileRepository.save(savedMasterTile);
                }
            }


            Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> new Role());
            admin.setName("ADMIN");
            admin = roleRepository.save(admin);

            for (User u : userRepository.findAll()) {
                if (!u.getRoles().contains(admin)) {
                    u.getRoles().add(admin);
                    userRepository.save(u);
                }
                tileService.provisioning(UserPrincipal.create(u));
            }

        }
    }

}
