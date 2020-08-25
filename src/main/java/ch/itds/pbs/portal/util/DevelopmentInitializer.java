package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.domain.Role;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.RoleRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.TileService;
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

    public DevelopmentInitializer(Environment environment, TileService tileService, UserRepository userRepository, RoleRepository roleRepository) {
        this.environment = environment;
        this.tileService = tileService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (environment.acceptsProfiles(Profiles.of("development"))) {

            tileService.init();

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
