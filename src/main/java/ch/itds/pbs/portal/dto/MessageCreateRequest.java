package ch.itds.pbs.portal.dto;

import ch.itds.pbs.portal.domain.LocalizedString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "The message must be provided with multiple localizations. The portal decides which localization is used based on the users profile. If the localized title or content is missing in the users language, no message will be sent.")
public class MessageCreateRequest {

    @Schema(description = "Title of the message", required = true)
    private LocalizedString title = new LocalizedString();

    @Schema(description = "Content of the message", required = true)
    private LocalizedString content = new LocalizedString();

    @Schema(description = "Optional URL related to the message")
    private LocalizedString url = new LocalizedString();

    @Schema(description = "The message can be limited to specified users. The ID is the one provided by MiData", required = true)
    private List<Long> limitToUserIds = new ArrayList<>();

}
