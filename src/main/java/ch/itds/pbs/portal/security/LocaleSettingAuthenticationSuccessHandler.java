package ch.itds.pbs.portal.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class LocaleSettingAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Resource
    private LocaleResolver localeResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setLocale(authentication, request, response);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected void setLocale(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                UserPrincipal localeProvider = (UserPrincipal) principal;
                Locale providedLocale = localeProvider.getLocale();
                localeResolver.setLocale(request, response, providedLocale);
            }
        }
    }
}
