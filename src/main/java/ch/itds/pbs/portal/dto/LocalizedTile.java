package ch.itds.pbs.portal.dto;

import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.FileMeta;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class LocalizedTile {

    private String title;
    private String content;
    private String url;
    private String category;

    private Color titleColor;
    private Color contentColor;
    private Color backgroundColor;

    private Long masterTileId;
    private int masterTileVersion;
    private Long userTileId;
    private Long categoryId;

    private FileMeta image;

    private int position = -1;
    private long unreadMessageCount = 0;

    public boolean getHasUrl() {
        return !StringUtils.isEmpty(url);
    }

}
