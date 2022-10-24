package ch.itds.pbs.portal.security.oauth;


import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.midata.MidataRawPermission;
import ch.itds.pbs.portal.exception.OAuth2AuthenticationProcessingException;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.repo.MidataPermissionRepository;
import ch.itds.pbs.portal.repo.MidataRoleRepository;
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

import java.util.*;

@Slf4j
@Service
public class MidataOAuth2UserService extends DefaultOAuth2UserService {

    private final transient UserRepository userRepository;
    private final transient MidataGroupRepository midataGroupRepository;
    private final transient MidataPermissionRepository midataPermissionRepository;
    private final transient MidataRoleRepository midataRoleRepository;

    public MidataOAuth2UserService(UserRepository userRepository, MidataGroupRepository midataGroupRepository, MidataPermissionRepository midataPermissionRepository, MidataRoleRepository midataRoleRepository) {
        super();
        this.userRepository = userRepository;
        this.midataGroupRepository = midataGroupRepository;
        this.midataPermissionRepository = midataPermissionRepository;
        this.midataRoleRepository = midataRoleRepository;
    }

    @Override
    @SuppressWarnings({"PMD.AvoidRethrowingException", "PMD.AvoidCatchingGenericException", "PMD.PreserveStackTrace"})
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {

        super.setRequestEntityConverter(new MidataOAuth2UserRequestEntityConverter());

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        MidataOAuth2UserInfo oAuth2UserInfo = new MidataOAuth2UserInfo(oAuth2User.getAttributes());
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByMidataUserId(oAuth2UserInfo.getId()).orElse(null);

        if (user == null) {
            user = registerNewUser(oAuth2UserInfo);
        } else {
            user = updateExistingUser(user, oAuth2UserInfo);
        }

        List<MidataPermission> existingPermissions = midataPermissionRepository.findByUser(user);
        Map<Long, MidataPermission> previousPermissionsById = new HashMap<>();
        for (MidataPermission p : existingPermissions) {
            previousPermissionsById.put(p.getId(), p);
        }

        for (MidataRawPermission permission : oAuth2UserInfo.getPermissions()) {
            MidataGroup group = midataGroupRepository.findByMidataId(permission.getGroupId());
            if (group == null) {
                group = new MidataGroup();
                group.setMidataId(permission.getGroupId());
                group.setName(permission.getGroupName());
                group = midataGroupRepository.save(group);
            }
            if (Objects.equals(group.getName(), permission.getGroupName())) {
                group.setName(permission.getGroupName());
                group = midataGroupRepository.save(group);
            }
            MidataRole role = midataRoleRepository.findByGroupAndName(group, permission.getRoleName());
            if (role == null) {
                role = new MidataRole();
                role.setGroup(group);
                role.setName(permission.getRoleName());
                role.setClazz(permission.getRoleClass());
            }
            if (Objects.equals(role.getName(), permission.getRoleName())) {
                role.setClazz(permission.getRoleClass());
                role = midataRoleRepository.save(role);
            }
            for (String p : permission.getPermissions()) {
                MidataPermission midataPermission = midataPermissionRepository.findByUserAndRoleAndPermission(user, role, p);
                if (midataPermission == null) {
                    midataPermission = new MidataPermission();
                    midataPermission.setUser(user);
                    midataPermission.setRole(role);
                    midataPermission.setPermission(p);
                    midataPermission = midataPermissionRepository.save(midataPermission);
                }
                previousPermissionsById.remove(midataPermission.getId());
            }

        }

        midataPermissionRepository.deleteAll(previousPermissionsById.values());

        return UserPrincipal.create(user);
    }


    private User registerNewUser(MidataOAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setMidataUserId(oAuth2UserInfo.getId());
        user.setUsername(Long.toString(oAuth2UserInfo.getId()));
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
        ensurePrimaryGroup(user, oAuth2UserInfo);
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, MidataOAuth2UserInfo oAuth2UserInfo) {
        existingUser.setUsername(Long.toString(oAuth2UserInfo.getId()));
        existingUser.setMidataUserId(oAuth2UserInfo.getId());
        existingUser.setMail(oAuth2UserInfo.getEmail());
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setNickName(oAuth2UserInfo.getName());
        ensurePrimaryGroup(existingUser, oAuth2UserInfo);
        return userRepository.save(existingUser);
    }

    private void ensurePrimaryGroup(User user, MidataOAuth2UserInfo oAuth2UserInfo) {
        if (oAuth2UserInfo.getPrimaryGroupId() != null) {
            MidataGroup primaryGroup = midataGroupRepository.findByMidataId(oAuth2UserInfo.getPrimaryGroupId());
            if (primaryGroup == null) {
                primaryGroup = new MidataGroup();
                primaryGroup.setMidataId(oAuth2UserInfo.getPrimaryGroupId());
                primaryGroup.setName("unknown group");
                primaryGroup = midataGroupRepository.save(primaryGroup);
            }
            user.setPrimaryMidataGroup(primaryGroup);
        }
    }
}
