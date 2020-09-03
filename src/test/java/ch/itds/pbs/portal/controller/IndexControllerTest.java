package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.dto.LocalizedTile;
import ch.itds.pbs.portal.service.LanguageService;
import ch.itds.pbs.portal.service.TileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IndexController.class)
@WithMockUser
public class IndexControllerTest extends BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    LanguageService languageService;

    @MockBean
    TileService tileService;

    @BeforeEach
    public void initMocks() {

        Mockito.when(languageService.convertToLanguage(any())).thenReturn(Language.DE);

        List<LocalizedTile> tiles = new ArrayList<>();
        LocalizedTile tile = new LocalizedTile();
        tile.setMasterTileVersion(1);
        tile.setPosition(0);
        tile.setBackgroundColor(Color.DEFAULT);
        tile.setTitle("Abc");
        tile.setContent("Abc");
        tiles.add(tile);

        Mockito.when(tileService.listTiles(any(), any())).thenReturn(tiles);


    }

    @Test
    public void regularIndex() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void indexAsAnonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

}
