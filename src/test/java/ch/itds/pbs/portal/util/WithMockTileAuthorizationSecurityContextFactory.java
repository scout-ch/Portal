package ch.itds.pbs.portal.util;

import ch.itds.pbs.portal.security.tile.TileAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockTileAuthorizationSecurityContextFactory implements WithSecurityContextFactory<WithMockTileAuthorization> {
    @Override
    public SecurityContext createSecurityContext(WithMockTileAuthorization tileAuth) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        TileAuthentication auth =
                new TileAuthentication(tileAuth.apiKey());
        auth.setAuthenticated(true);
        auth.setTileId(tileAuth.tileId());
        context.setAuthentication(auth);
        return context;
    }
}
