package ch.itds.pbs.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema
public class TileOverrideCreateResponse {

    @Schema(description = "internal ID of the override")
    private Long id;

}
