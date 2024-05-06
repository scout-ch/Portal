package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.conf.DevelopmentBootstrapData;
import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.repo.*;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.TileService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Component
public class DevelopmentInitializer {

    private final transient Environment environment;
    private final transient TileService tileService;
    private final transient UserRepository userRepository;
    private final transient RoleRepository roleRepository;
    private final transient MasterTileRepository masterTileRepository;
    private final transient CategoryRepository categoryRepository;

    private final transient MidataGroupRepository midataGroupRepository;
    private final transient DevelopmentBootstrapData developmentBootstrapData;

    public DevelopmentInitializer(Environment environment, TileService tileService, UserRepository userRepository, RoleRepository roleRepository, MasterTileRepository masterTileRepository, CategoryRepository categoryRepository, MidataGroupRepository midataGroupRepository, DevelopmentBootstrapData developmentBootstrapData) {
        this.environment = environment;
        this.tileService = tileService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.masterTileRepository = masterTileRepository;
        this.categoryRepository = categoryRepository;
        this.midataGroupRepository = midataGroupRepository;
        this.developmentBootstrapData = developmentBootstrapData;
    }

    @PostConstruct
    public void init() {
        if (environment.acceptsProfiles(Profiles.of("development", "test"))) {

            if (developmentBootstrapData.getMidataGroups() != null) {
                for (MidataGroup g : developmentBootstrapData.getMidataGroups()) {
                    MidataGroup savedGroup = midataGroupRepository.findByMidataId(g.getMidataId());
                    BeanUtils.copyProperties(g, savedGroup, "id", "version", "dateCreated", "lastUpdated", "roles", "defaultTiles");
                    midataGroupRepository.save(savedGroup);
                }
            }
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
                    mt.setMidataGroupOnly(midataGroupRepository.findByMidataId(mt.getMidataGroupOnly().getMidataId()));
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
