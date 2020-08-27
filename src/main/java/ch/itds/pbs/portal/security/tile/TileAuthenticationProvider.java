package ch.itds.pbs.portal.security.tile;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.exception.InvalidTileApiKeyException;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class TileAuthenticationProvider implements AuthenticationProvider {

    private final transient MasterTileRepository masterTileRepository;

    public TileAuthenticationProvider(MasterTileRepository masterTileRepository) {
        this.masterTileRepository = masterTileRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        TileAuthentication tileAuthentication = (TileAuthentication) authentication;
        String key = tileAuthentication.getApiKey();
        MasterTile masterTile = masterTileRepository.findEnabledByApiKey(key);
        if (masterTile != null) {
            tileAuthentication.setAuthenticated(true);
            tileAuthentication.setTileId(masterTile.getId());
            return tileAuthentication;
        }
        throw new InvalidTileApiKeyException("invalid api key");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TileAuthentication.class.isAssignableFrom(authentication);
    }
}
