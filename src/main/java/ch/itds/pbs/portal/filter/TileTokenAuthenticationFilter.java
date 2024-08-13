package ch.itds.pbs.portal.filter;

import ch.itds.pbs.portal.exception.InvalidTileApiKeyException;
import ch.itds.pbs.portal.security.tile.TileAuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class TileTokenAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    public TileTokenAuthenticationFilter(TileAuthenticationManager tileAuthenticationManager) {
        this.setAuthenticationManager(tileAuthenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String apiKey = (String) getPreAuthenticatedCredentials(request);
        if (StringUtils.hasText(apiKey)) {
            return apiKey;
        } else {
            throw new InvalidTileApiKeyException("missing api key");
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader("X-Tile-Authorization");
    }
}
