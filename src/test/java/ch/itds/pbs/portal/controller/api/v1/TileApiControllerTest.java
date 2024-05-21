package ch.itds.pbs.portal.controller.api.v1;

import ch.itds.pbs.portal.conf.SecurityConfig;
import ch.itds.pbs.portal.controller.BaseControllerTest;
import ch.itds.pbs.portal.domain.TileOverride;
import ch.itds.pbs.portal.service.TileService;
import ch.itds.pbs.portal.util.WithMockTileAuthorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TileEndpoint.class)
@WithMockTileAuthorization(apiKey = "TILE-KEY-01", tileId = 3L)
@Import({SecurityConfig.class})
public class TileApiControllerTest extends BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    TileService tileService;

    @BeforeEach
    public void initMocks() {
        TileOverride tileOverride = new TileOverride();
        tileOverride.setId(42L);
        Mockito.when(tileService.createTileOverride(any(), any())).thenReturn(tileOverride);
    }

    @Test
    public void override() throws Exception {

        String rcontent = "{\"title\": {\"de\": \"Title\"},\"content\": {\"de\": \"Content...\"}, \"limitToUserIds\": [1,2,3], \"validUntil\": \"2040-01-01T12:00:00.000Z\"}";

        mockMvc.perform(put("/api/v1/tile/override")
                        .content(rcontent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42L));
    }

}
