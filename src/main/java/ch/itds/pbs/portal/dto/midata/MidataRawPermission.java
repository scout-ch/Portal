package ch.itds.pbs.portal.dto.midata;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Builder
public class MidataRawPermission {

    private Integer groupId;
    private String groupName;
    private String roleName;
    private String roleClass;
    private ArrayList<String> permissions;

}
