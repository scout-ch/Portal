package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.UserService;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Locale;

public class UserUpdatingSessionLocaleResolver extends SessionLocaleResolver {

    private final transient UserService userService;

    public UserUpdatingSessionLocaleResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        super.setLocaleContext(request, response, localeContext);
        Principal principal = request.getUserPrincipal();
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) principal).getPrincipal();
            if (oAuth2User instanceof UserPrincipal) {
                Locale locale = localeContext.getLocale();
                if (locale != null) {
                    userService.setLanguage((UserPrincipal) oAuth2User, locale);
                }
            }
        }
    }
}
