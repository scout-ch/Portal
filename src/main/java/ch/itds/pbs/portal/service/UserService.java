package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.Role;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.RoleRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final transient UserRepository userRepository;
    private final transient RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void setLanguage(UserPrincipal userPrincipal, Locale locale) {
        Language language = Language.valueOfOrDefault(locale.getLanguage().toUpperCase());
        userRepository.setLanguage(userPrincipal.getId(), language);
    }

    public void updateRoles(String username, List<String> roleNames) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Role> roles = new HashSet<>();
            for (String roleNameShort : roleNames) {
                String roleName = "ROLE_" + roleNameShort;
                Optional<Role> optRole = roleRepository.findByName(roleName);
                if (optRole.isPresent()) {
                    roles.add(optRole.get());
                } else {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    roles.add(role);
                }
            }
            user.setRoles(roles);
            try {
                userRepository.save(user);
            } catch (ObjectOptimisticLockingFailureException oollfe) {
                log.info("unable to save user {}/{}: {}", user.getMidataUserId(), user.getMail(), oollfe.getMessage());
            }
        }
    }

    public List<String> extractRoleNames(ClaimAccessor principal) {
        List<String> list = new ArrayList<>();
        if (principal.hasClaim("crs-intranet-roles")) {
            for (String rawRole : principal.getClaimAsStringList("crs-intranet-roles")) {
                String cleanRole = rawRole.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
                list.add(cleanRole);
            }

        }
        return list;
    }
}
