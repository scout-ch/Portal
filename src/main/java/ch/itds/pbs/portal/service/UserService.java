package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void setLanguage(UserPrincipal userPrincipal, Locale locale) {
        Language language = Language.valueOfOrDefault(locale.getLanguage().toUpperCase());
        userRepository.setLanguage(userPrincipal.getId(), language);
    }
}
