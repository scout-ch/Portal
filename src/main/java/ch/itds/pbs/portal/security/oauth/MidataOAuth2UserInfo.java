package ch.itds.pbs.portal.security.oauth;

import java.util.Map;

public class MidataOAuth2UserInfo {

    private Map<String, Object> attributes;

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

}