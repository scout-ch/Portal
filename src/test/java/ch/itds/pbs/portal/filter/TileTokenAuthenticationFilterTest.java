package ch.itds.pbs.portal.filter;

import ch.itds.pbs.portal.security.tile.TileAuthentication;
import ch.itds.pbs.portal.security.tile.TileAuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class TileTokenAuthenticationFilterTest {

    @MockBean
    TileAuthenticationProvider tileAuthenticationProvider;

    @BeforeEach
    void prepare() {
        Mockito.when(tileAuthenticationProvider.authenticate(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void doFilterInternal() throws ServletException, IOException {

        TileTokenAuthenticationFilter filter = new TileTokenAuthenticationFilter(tileAuthenticationProvider);

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