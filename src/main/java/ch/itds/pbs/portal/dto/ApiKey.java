package ch.itds.pbs.portal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ApiKey extends ActionMessage {

    private String apiKey;
}
