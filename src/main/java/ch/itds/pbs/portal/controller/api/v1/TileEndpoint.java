package ch.itds.pbs.portal.controller.api.v1;

import ch.itds.pbs.portal.domain.TileOverride;
import ch.itds.pbs.portal.dto.TileOverrideCreateRequest;
import ch.itds.pbs.portal.dto.TileOverrideCreateResponse;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.tile.TileAuthentication;
import ch.itds.pbs.portal.service.TileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
@Tag(name = "Tile")
@PreAuthorize("isFullyAuthenticated() and hasRole('TILE')")
public class TileEndpoint {

    private final transient TileService tileService;

    public TileEndpoint(TileService tileService) {
        this.tileService = tileService;
    }

    @Operation(description = "Override a tile for some time")
    @PutMapping("/tile/override")
    @Transactional
    public ResponseEntity<TileOverrideCreateResponse> override(@RequestBody TileOverrideCreateRequest tileOverrideCreateRequest, @CurrentUser TileAuthentication authentication) {
        TileOverride override = tileService.createTileOverride(authentication.getTileId(), tileOverrideCreateRequest);

        return ResponseEntity.ok(TileOverrideCreateResponse.builder().id(override.getId()).build());

    }

}
