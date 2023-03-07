package ch.itds.pbs.portal.security.oauth;


import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.midata.MidataRawPermission;
import ch.itds.pbs.portal.exception.OAuth2AuthenticationProcessingException;
import ch.itds.pbs.portal.repo.*;
import ch.itds.pbs.portal.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
public class MidataOAuth2UserService extends DefaultOAuth2UserService {

    private final transient UserRepository userRepository;

    private final transient RoleRepository roleRepository;
    private final transient MidataGroupRepository midataGroupRepository;
    private final transient MidataPermissionRepository midataPermissionRepository;
    private final transient MidataRoleRepository midataRoleRepository;

    private final transient CategoryRepository categoryRepository;

    public MidataOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository, MidataGroupRepository midataGroupRepository, MidataPermissionRepository midataPermissionRepository, MidataRoleRepository midataRoleRepository, CategoryRepository categoryRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.midataGroupRepository = midataGroupRepository;
        this.midataPermissionRepository = midataPermissionRepository;
        this.midataRoleRepository = midataRoleRepository;
        this.categoryRepository = categoryRepository;
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
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByMidataUserId(oAuth2UserInfo.getId()).orElse(null);

        if (user == null) {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        } else {
            user = updateExistingUser(user, oAuth2UserRequest, oAuth2UserInfo);
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
                group = midataGroupRepository.saveAndFlush(group);
            }
            if (!Objects.equals(group.getName(), permission.getGroupName())) {
                group.setName(permission.getGroupName());
                group = midataGroupRepository.save(group);
            }
            List<Category> groupCategories = categoryRepository.findAllByMidataGroupOnly(group);
            if (groupCategories.isEmpty()) {
                Category category = new Category();
                LocalizedString name = new LocalizedString();
                name.setDe(group.getName());
                name.setFr(group.getName());
                name.setIt(group.getName());
                name.setEn(group.getName());
                category.setName(name);
                category.setMidataGroupOnly(group);
                categoryRepository.save(category);
            } else if (groupCategories.size() == 1) {
                Category category = groupCategories.get(0);
                boolean hasChanges = false;
                if (category.getName() == null) {
                    category.setName(new LocalizedString());
                    hasChanges = true;
                }
                if (Objects.equals(category.getName().getDe(), group.getName())) {
                    category.getName().setDe(group.getName());
                    hasChanges = true;
                }
                if (Objects.equals(category.getName().getFr(), group.getName())) {
                    category.getName().setFr(group.getName());
                    hasChanges = true;
                }
                if (Objects.equals(category.getName().getIt(), group.getName())) {
                    category.getName().setIt(group.getName());
                    hasChanges = true;
                }
                if (Objects.equals(category.getName().getEn(), group.getName())) {
                    category.getName().setEn(group.getName());
                    hasChanges = true;
                }
                if (hasChanges) {
                    categoryRepository.save(category);
                }
            }

            MidataRole role = midataRoleRepository.findByGroupAndName(group, permission.getRoleName());
            if (role == null) {
                role = new MidataRole();
                role.setGroup(group);
                role.setName(permission.getRoleName());
                role.setClazz(permission.getRoleClass());
                role = midataRoleRepository.saveAndFlush(role);
            }
            if (!Objects.equals(role.getClazz(), permission.getRoleClass())) {
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


    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, MidataOAuth2UserInfo oAuth2UserInfo) {
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
        ensureUserRole(user);
        ensurePrimaryGroup(user, oAuth2UserInfo);
        ensureGroupHierarchy(user, oAuth2UserRequest);
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserRequest oAuth2UserRequest, MidataOAuth2UserInfo oAuth2UserInfo) {
        existingUser.setUsername(Long.toString(oAuth2UserInfo.getId()));
        existingUser.setMidataUserId(oAuth2UserInfo.getId());
        existingUser.setMail(oAuth2UserInfo.getEmail());
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setNickName(oAuth2UserInfo.getName());
        ensureUserRole(existingUser);
        ensurePrimaryGroup(existingUser, oAuth2UserInfo);
        ensureGroupHierarchy(existingUser, oAuth2UserRequest);
        return userRepository.save(existingUser);
    }

    private void ensureUserRole(User user) {
        roleRepository.findByName("USER").ifPresent(userRole -> user.getRoles().add(userRole));
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

    private void ensureGroupHierarchy(User user, OAuth2UserRequest oAuth2UserRequest) {
        if (user.getPrimaryMidataGroup() != null) {
            try {
                Integer[] hierarchy = getGroupHierarchy(oAuth2UserRequest, user.getLanguage().name().toLowerCase(Locale.ROOT), user.getPrimaryMidataGroup().getMidataId());
                user.setMidataGroupHierarchy(hierarchy);
            } catch (Exception e) {
                log.warn("unable to sync group hierarchy for {} with lang={} and primaryGroupId={}: {}", user.getUsername(), user.getLanguage().name().toLowerCase(Locale.ROOT), user.getPrimaryMidataGroup().getMidataId(), e.getMessage());
            }
        }
    }

    private Integer[] getGroupHierarchy(OAuth2UserRequest userRequest, String lang, long primaryGroupId) {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        HttpMethod httpMethod = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        URI uri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                .replacePath("/" + lang + "/groups/" + primaryGroupId + ".json")
                .build()
                .toUri();

        for (String scope : userRequest.getClientRegistration().getScopes()) {
            headers.add("X-Scope", scope);
        }

        RequestEntity<?> request;
        headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
        request = new RequestEntity<>(headers, httpMethod, uri);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(request, new ParameterizedTypeReference<>() {
        });

        TreeSet<Integer> hierarchy = new TreeSet<>();
        Map<String, Object> responseBody = response.getBody();
        if (responseBody.get("groups") instanceof List groups) {
            if (groups.size() == 1) {
                if (groups.get(0) instanceof Map groupDetails) {
                    if (groupDetails.get("links") instanceof Map links) {
                        if (links.get("hierarchies") instanceof List groupIdList) {
                            for (Object groupId : groupIdList) {
                                if (groupId instanceof String groupIdStr) {
                                    hierarchy.add(Integer.parseInt(groupIdStr));
                                }
                            }
                        }
                    }
                }
            }
        }

        return hierarchy.descendingSet().toArray(hierarchy.toArray(new Integer[0]));
    }
}
