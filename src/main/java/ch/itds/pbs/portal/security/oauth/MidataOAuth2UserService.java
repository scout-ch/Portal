package ch.itds.pbs.portal.security.oauth;


import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.exception.OAuth2AuthenticationProcessingException;
import ch.itds.pbs.portal.repo.RoleRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;

@Slf4j
@Service
public class MidataOAuth2UserService extends DefaultOAuth2UserService {

    private final transient UserRepository userRepository;

    private final transient RoleRepository roleRepository;

    public MidataOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @SuppressWarnings({"PMD.AvoidRethrowingException", "PMD.AvoidCatchingGenericException", "PMD.PreserveStackTrace"})
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {

        super.setRequestEntityConverter(new MidataOAuth2UserRequestEntityConverter());

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        MidataOAuth2UserInfo oAuth2UserInfo = new MidataOAuth2UserInfo(oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByMidataUserId(oAuth2UserInfo.getId()).orElse(null);

        if (user == null) {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        } else {
            user = updateExistingUser(user, oAuth2UserInfo);
        }

        return UserPrincipal.create(user);
    }


    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, MidataOAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setMidataUserId(oAuth2UserInfo.getId());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setMail(oAuth2UserInfo.getEmail());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setNickName(oAuth2UserInfo.getName());
        user.setLanguage(Language.valueOfOrDefault(oAuth2UserInfo.getCorrespondenceLanguage()));
        user.setEnabled(true);
        user.setAccountExpired(false);
        user.setAccountLocked(false);
        user.setPasswordExpired(false);
        user.setRoles(new HashSet<>());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, MidataOAuth2UserInfo oAuth2UserInfo) {
        existingUser.setUsername(oAuth2UserInfo.getEmail());
        existingUser.setMidataUserId(oAuth2UserInfo.getId());
        existingUser.setMail(oAuth2UserInfo.getEmail());
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setNickName(oAuth2UserInfo.getName());
        return userRepository.save(existingUser);
    }
}