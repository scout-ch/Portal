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
}
