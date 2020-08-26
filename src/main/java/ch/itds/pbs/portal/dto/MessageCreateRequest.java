package ch.itds.pbs.portal.dto;

import ch.itds.pbs.portal.domain.LocalizedString;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageCreateRequest {

    private LocalizedString title = new LocalizedString();
    private LocalizedString content = new LocalizedString();
    private LocalizedString url = new LocalizedString();

    private List<Long> limitToUserIds = new ArrayList<>();

}
