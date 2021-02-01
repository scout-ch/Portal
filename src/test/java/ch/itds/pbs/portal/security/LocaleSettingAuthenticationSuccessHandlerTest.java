package ch.itds.pbs.portal.security;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LocaleSettingAuthenticationSuccessHandlerTest {

    @Autowired
    LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler;

    @Autowired
    LocaleResolver localeResolver;

    @Test
    void onAuthenticationSuccessForConfiguredLanguage() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        User u = new User();
        u.setId(1L);
        u.setLanguage(Language.FR);
        u.setRoles(new HashSet<>());
        UserPrincipal up = UserPrincipal.create(u);

        Authentication authentication = new TestingAuthenticationToken(up, "password");

        localeSettingAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(Language.FR.name().toLowerCase(Locale.ROOT), localeResolver.resolveLocale(request).getLanguage());
    }

    @Test
    void onAuthenticationSuccessForDefaultLanguage() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        User u = new User();
        u.setId(1L);
        u.setRoles(new HashSet<>());
        UserPrincipal up = UserPrincipal.create(u);
        Authentication authentication = new TestingAuthenticationToken(up, "password");

        localeSettingAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(Language.DE.name().toLowerCase(Locale.ROOT), localeResolver.resolveLocale(request).getLanguage());
    }

}