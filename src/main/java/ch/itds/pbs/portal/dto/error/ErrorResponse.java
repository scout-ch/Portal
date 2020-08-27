package ch.itds.pbs.portal.dto.error;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "generic error message")
public class ErrorResponse {

    @Schema(description = "HTTP status code", required = true)
    int code;

    @Schema(description = "optional message")
    String message;
}
