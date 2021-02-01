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
import org.springframework.util.StringUtils;

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
        if (environment.acceptsProfiles(Profiles.of("development", "test"))) {

            if (developmentBootstrapData.getCategories() != null) {
                for (Category c : developmentBootstrapData.getCategories()) {
                    Category savedCategory = categoryRepository.findByNameDe(c.getName().getDe()).orElseGet(Category::new);
                    BeanUtils.copyProperties(c, savedCategory, "id", "version", "dateCreated", "lastUpdated", "tiles");
                    categoryRepository.save(savedCategory);
                }
            }
            if (developmentBootstrapData.getMasterTiles() != null) {
                for (MasterTile mt : developmentBootstrapData.getMasterTiles()) {
                    mt.setCategory(categoryRepository.findByNameDe(mt.getCategory().getName().getDe()).orElse(null));

                    MasterTile savedMasterTile;
                    if (StringUtils.hasText(mt.getTitle().getDe())) {
                        savedMasterTile = masterTileRepository.findFirstByTitleDe(mt.getTitle().getDe()).orElseGet(MasterTile::new);
                    } else if (StringUtils.hasText(mt.getTitle().getEn())) {
                        savedMasterTile = masterTileRepository.findFirstByTitleEn(mt.getTitle().getEn()).orElseGet(MasterTile::new);
                    } else {
                        throw new RuntimeException("bootstrap tile must have DE or EN title");
                    }
                    BeanUtils.copyProperties(mt, savedMasterTile, "id", "version", "dateCreated", "lastUpdated", "image");
                    masterTileRepository.save(savedMasterTile);
                }
            }


            Role admin = roleRepository.findByName("ADMIN").orElseGet(Role::new);
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
