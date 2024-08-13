package ch.itds.pbs.portal.security.tile;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.exception.InvalidTileApiKeyException;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TileAuthenticationManager implements AuthenticationManager {

    private final transient MasterTileRepository masterTileRepository;

    public TileAuthenticationManager(MasterTileRepository masterTileRepository) {
        this.masterTileRepository = masterTileRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken) {
            Object principal = preAuthenticatedAuthenticationToken.getPrincipal();
            if (principal instanceof String apiKey) {
                MasterTile masterTile = masterTileRepository.findEnabledByApiKey(apiKey);
                if (masterTile != null) {
                    TileAuthentication tileAuthentication = new TileAuthentication(apiKey);
                    tileAuthentication.setAuthenticated(true);
                    tileAuthentication.setTileId(masterTile.getId());
                    return tileAuthentication;
                }
                throw new InvalidTileApiKeyException("invalid api key");
            }
        }
        throw new InvalidTileApiKeyException("invalid api key");
    }
}
