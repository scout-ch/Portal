package ch.itds.pbs.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema
public class MessageCreateResponse {

    @Schema(description = "number of created messages")
    private int messagesCreated;

}
