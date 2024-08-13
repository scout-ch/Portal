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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class TileAuthenticationManagerTest {

    @Autowired
    TileAuthenticationManager tileAuthenticationManager;

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
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken("valid-key", "valid-key");
        Authentication result = tileAuthenticationManager.authenticate(token);
        assertTrue(result.isAuthenticated());
        assertEquals(TileAuthentication.class, result.getClass());
        assertEquals(TileAuthentication.class, result.getPrincipal().getClass());
        assertEquals(3302L, ((TileAuthentication) result.getPrincipal()).tileId);
    }

    @Test
    void authenticationFailureNoKey() {
        assertThrows(InvalidTileApiKeyException.class, () -> {
            tileAuthenticationManager.authenticate(null);
        });
    }

    @Test
    void authenticationFailureInvalidKeyClass() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("valid-key", "valid-key");
        assertThrows(InvalidTileApiKeyException.class, () -> {
            tileAuthenticationManager.authenticate(token);
        });
    }

    @Test
    void authenticationFailureInvalidKeyValue() {
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken("invalid-key", "invalid-key");
        assertThrows(InvalidTileApiKeyException.class, () -> {
            tileAuthenticationManager.authenticate(token);
        });
    }

    @Test
    void authenticationFailureEmptyKeyValue() {
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(null, null);
        assertThrows(InvalidTileApiKeyException.class, () -> {
            tileAuthenticationManager.authenticate(token);
        });
    }
}