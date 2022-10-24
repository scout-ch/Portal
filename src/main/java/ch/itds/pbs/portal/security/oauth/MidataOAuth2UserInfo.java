package ch.itds.pbs.portal.security.oauth;

import ch.itds.pbs.portal.dto.midata.MidataRawPermission;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MidataOAuth2UserInfo {

    private final Map<String, Object> attributes;

    public MidataOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Long getId() {
        return Long.valueOf((Integer) attributes.get("id"));
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getName() {
        return (String) attributes.get("nickname");
    }

    public String getFirstName() {
        return (String) attributes.get("first_name");
    }

    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    public String getCorrespondenceLanguage() {
        return (String) attributes.get("correspondence_language");
    }

    public Integer getPrimaryGroupId() {
        return (Integer) attributes.get("primary_group_id");
    }

    public List<MidataRawPermission> getPermissions() {
        List<MidataRawPermission> permissions = new ArrayList<>();
        ArrayList<LinkedHashMap<String, Object>> rawRoles = (ArrayList<LinkedHashMap<String, Object>>) attributes.get("roles");
        if (rawRoles != null && !rawRoles.isEmpty()) {
            for (LinkedHashMap<String, Object> entry : rawRoles) {
                permissions.add(MidataRawPermission.builder()
                        .groupId((Integer) entry.get("group_id"))
                        .groupName((String) entry.get("group_name"))
                        .roleName((String) entry.get("role_name"))
                        .roleClass((String) entry.get("role_class"))
                        .permissions((ArrayList<String>) entry.get("permissions"))
                        .build());
            }
        }
        return permissions;
    }

}
