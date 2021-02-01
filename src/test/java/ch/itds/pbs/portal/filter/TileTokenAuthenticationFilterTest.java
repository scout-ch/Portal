package ch.itds.pbs.portal.filter;

import ch.itds.pbs.portal.security.tile.TileAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileTokenAuthenticationFilterTest {

    @Test
    void doFilterInternal() throws ServletException, IOException {

        TileTokenAuthenticationFilter filter = new TileTokenAuthenticationFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        request.addHeader("X-Tile-Authorization", "my-token");

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertEquals(TileAuthentication.class, auth.getClass());
        assertEquals("my-token", ((TileAuthentication) auth).getApiKey());

    }
}