package ch.itds.pbs.portal.filter;

import ch.itds.pbs.portal.security.tile.TileAuthentication;
import ch.itds.pbs.portal.security.tile.TileAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TileTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TileAuthenticationProvider tileAuthenticationProvider;

    public TileTokenAuthenticationFilter(TileAuthenticationProvider tileAuthenticationProvider) {
        this.tileAuthenticationProvider = tileAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-Tile-Authorization");

        if (apiKey != null) {

            Authentication auth = new TileAuthentication(apiKey);
            SecurityContextHolder.getContext().setAuthentication(tileAuthenticationProvider.authenticate(auth));
        }

        filterChain.doFilter(request, response);
    }
}
