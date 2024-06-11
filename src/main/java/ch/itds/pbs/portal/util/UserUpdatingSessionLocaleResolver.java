package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.UserService;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        if (principal instanceof Authentication) {
            Object authenticatedPrincipal = ((Authentication) principal).getPrincipal();
            if (authenticatedPrincipal instanceof UserPrincipal) {
                Locale locale = localeContext.getLocale();
                if (locale != null) {
                    userService.setLanguage((UserPrincipal) authenticatedPrincipal, locale);
                }
            }
        }
    }
}
