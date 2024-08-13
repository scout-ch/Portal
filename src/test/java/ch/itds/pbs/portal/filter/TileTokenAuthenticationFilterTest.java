package ch.itds.pbs.portal.filter;

import ch.itds.pbs.portal.exception.InvalidTileApiKeyException;
import ch.itds.pbs.portal.security.tile.TileAuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class TileTokenAuthenticationFilterTest {

    @MockBean
    TileAuthenticationManager tileAuthenticationManager;

    @BeforeEach
    void prepare() {
        Mockito.when(tileAuthenticationManager.authenticate(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void preAuthenticatedFromToken() {

        TileTokenAuthenticationFilter filter = new TileTokenAuthenticationFilter(tileAuthenticationManager);

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("X-Tile-Authorization", "my-token");

        String returnedPrincipal = String.valueOf(filter.getPreAuthenticatedPrincipal(request));
        String returnedCredentials = String.valueOf(filter.getPreAuthenticatedCredentials(request));

        assertEquals("my-token", returnedPrincipal);
        assertEquals("my-token", returnedCredentials);

    }

    @Test
    void preAuthenticatedWithoutToken() {

        TileTokenAuthenticationFilter filter = new TileTokenAuthenticationFilter(tileAuthenticationManager);

        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(InvalidTileApiKeyException.class, () -> filter.getPreAuthenticatedPrincipal(request));
        assertNull(filter.getPreAuthenticatedCredentials(request));

    }
}