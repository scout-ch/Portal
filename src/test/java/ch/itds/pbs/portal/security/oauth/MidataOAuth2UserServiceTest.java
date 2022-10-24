package ch.itds.pbs.portal.security.oauth;

import ch.itds.pbs.portal.WireMockInitializer;
import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.*;
import ch.itds.pbs.portal.security.UserPrincipal;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {WireMockInitializer.class})
class MidataOAuth2UserServiceTest {

    @Autowired
    MidataOAuth2UserService midataOAuth2UserService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTileRepository userTileRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MidataGroupRepository midataGroupRepository;

    @Autowired
    MidataRoleRepository midataRoleRepository;

    @Autowired
    MidataPermissionRepository midataPermissionRepository;

    /**
     * Test MidataOAuth2UserService
     * * remove user 3110 with it's messages and tiles if existing
     * * load user (will be created based on mocked user profile)
     * * ensure user profile data is saved to the database
     * * load user again (will be updated based on changed mocked user profile containing midata permissions and primary group)
     * * ensure user profile data is updated in the database
     */
    @Test
    void loadUserWithCreateAndUpdate() {

        userRepository.findByUsername("3110").ifPresent((u) -> {
            midataPermissionRepository.deleteAll(midataPermissionRepository.findByUser(u));
            messageRepository.deleteAll(messageRepository.findAllCompleteByUserId(u.getId()));
            userTileRepository.deleteAll(userTileRepository.findAllByUser(u));
            userRepository.delete(u);
        });

        wireMockServer.stubFor(get(urlPathEqualTo("/oauth2/user"))
                .willReturn(okJson("{\"id\":3110,\"email\":\"oauth@example.com\",\"nickname\":\"oAuth\",\"first_name\":\"oAuthilius\",\"last_name\":\"Testfamily\",\"correspondence_language\":\"fr\"}")));

        ClientRegistration reg = ClientRegistration.withRegistrationId("wm-reg")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("test")
                .clientSecret("test")
                .authorizationUri(wireMockServer.baseUrl() + "/oauth2/authorization")
                .userInfoUri(wireMockServer.baseUrl() + "/oauth2/user")
                .tokenUri(wireMockServer.baseUrl() + "/oauth2/token")
                .redirectUri(wireMockServer.baseUrl() + "/oauth2/redirect")
                .userNameAttributeName("email")
                .scope("with_roles")
                .build();

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token-value", LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().toInstant(ZoneOffset.UTC));

        OAuth2UserRequest req = new OAuth2UserRequest(reg, token);

        UserPrincipal oAuth2User = (UserPrincipal) midataOAuth2UserService.loadUser(req);

        User user = userRepository.findByUsername(oAuth2User.getUsername()).get();
        assertEquals("oauth@example.com", user.getMail());
        assertEquals("Testfamily", user.getLastName());
        assertEquals("oAuthilius", user.getFirstName());
        assertEquals("oAuth", user.getNickName());
        assertEquals(Language.FR, user.getLanguage());

        wireMockServer.stubFor(get(urlPathEqualTo("/oauth2/user"))
                .willReturn(okJson("{\"id\":3110,\"email\":\"oauth-married@example.com\",\"nickname\":\"oAuth\",\"first_name\":\"oAuthilius\",\"last_name\":\"Testfamily - Married\",\"correspondence_language\":\"de\",\"primary_group_id\": 42,\"roles\": [{\"group_id\": 84, \"group_name\": \"Testgruppe\", \"role_name\": \"IT Support\", \"role_class\": \"Group::Bund::ItSupport\",\n" +
                        "  \"permissions\": [\"layer_and_below_full\",\"admin\"]\n" +
                        "}]}")));


        oAuth2User = (UserPrincipal) midataOAuth2UserService.loadUser(req);

        user = userRepository.findByUsernameWithPrimaryMidataGroupAndMidataPermissions(oAuth2User.getUsername()).get();
        assertEquals("oauth-married@example.com", user.getMail());
        assertEquals("Testfamily - Married", user.getLastName());
        assertEquals("oAuthilius", user.getFirstName());
        assertEquals("oAuth", user.getNickName());
        assertEquals(Language.FR, user.getLanguage()); // language should not be updated!
        assertEquals(42, user.getPrimaryMidataGroup().getMidataId());
        assertEquals(2, user.getMidataPermissions().size());

        userRepository.findByUsername("3110").ifPresent((u) -> userRepository.delete(u));
    }
}
