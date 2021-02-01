package ch.itds.pbs.portal.security.tile;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.exception.InvalidTileApiKeyException;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class TileAuthenticationProviderTest {

    @Autowired
    TileAuthenticationProvider tileAuthenticationProvider;

    @MockBean
    MasterTileRepository masterTileRepository;

    @BeforeEach
    public void initMocks() {
        MasterTile mt = new MasterTile();
        mt.setId(3302L);
        mt.setApiKey("valid-key");
        Mockito.when(masterTileRepository.findEnabledByApiKey(eq("valid-key"))).thenReturn(mt);
        Mockito.when(masterTileRepository.findEnabledByApiKey(not(eq("valid-key")))).thenReturn(null);
    }

    @Test
    void authenticate() {
        TileAuthentication authentication = new TileAuthentication("valid-key");
        Authentication result = tileAuthenticationProvider.authenticate(authentication);
        assertTrue(result.isAuthenticated());
        assertEquals(TileAuthentication.class, result.getClass());
        assertEquals(TileAuthentication.class, result.getPrincipal().getClass());
        assertEquals(3302L, ((TileAuthentication) result.getPrincipal()).tileId);
    }

    @Test
    void authenticationFailure() {
        TileAuthentication authentication = new TileAuthentication("invalid-key");
        assertThrows(InvalidTileApiKeyException.class, () -> {
            tileAuthenticationProvider.authenticate(authentication);
        });
    }

    @Test
    void supports() {
        assertTrue(tileAuthenticationProvider.supports(TileAuthentication.class));
    }
}