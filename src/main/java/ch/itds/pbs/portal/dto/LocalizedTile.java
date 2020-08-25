package ch.itds.pbs.portal.dto;

import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.FileMeta;
import lombok.Data;

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
    private Long userTileId;
    private Long categoryId;

    private FileMeta image;

    private int position = -1;
    private int messageCount = 0;


}
