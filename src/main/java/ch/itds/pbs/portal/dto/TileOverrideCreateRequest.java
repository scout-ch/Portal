package ch.itds.pbs.portal.dto;

import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.LocalizedString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "The override tile must be provided with multiple localizations. The portal decides which localization is used based on the users profile. If the localized title or content is missing in the users language, no override will be visible.")
public class TileOverrideCreateRequest {

    @Schema(description = "Lifespan of the override", required = true)
    LocalDateTime validUntil;
    @Schema(description = "Title of the override tile", required = true)
    private LocalizedString title = new LocalizedString();
    @Schema(description = "Content of the override tile", required = true)
    private LocalizedString content = new LocalizedString();
    @Schema(description = "Optional URL related to the override tile")
    private LocalizedString url = new LocalizedString();
    @Schema(description = "The override can be limited to specified users. The ID is the one provided by MiData")
    private List<Long> limitToUserIds = new ArrayList<>();
    @Schema(description = "Optional theme related to the override tile")
    private Color backgroundColor = Color.DEFAULT;

}
