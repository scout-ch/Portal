package ch.itds.pbs.portal.controller.api.v1;

import ch.itds.pbs.portal.controller.BaseControllerTest;
import ch.itds.pbs.portal.util.WithMockTileAuthorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageEndpoint.class)
@WithMockTileAuthorization(apiKey = "TILE-KEY-01", tileId = 3L)
public class MessageApiControllerTest extends BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void initMocks() {

        Mockito.when(messageService.createMessages(any(), any())).thenReturn(3);
    }

    @Test
    public void postMessage() throws Exception {

        String rcontent = "{\"title\": {\"de\": \"Title\"},\"content\": {\"de\": \"Content...\"}, limitToUserIds:[1,2,3]}";

        mockMvc.perform(put("/api/v1/message")
                .content(rcontent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messagesCreated").value(3));
    }

}
