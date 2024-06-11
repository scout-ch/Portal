package ch.itds.pbs.portal.controller.api.v1;

import ch.itds.pbs.portal.dto.MessageCreateRequest;
import ch.itds.pbs.portal.dto.MessageCreateResponse;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.tile.TileAuthentication;
import ch.itds.pbs.portal.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
@Tag(name = "Message")
public class MessageEndpoint {

    private final transient MessageService messageService;

    public MessageEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(description = "Send a message to the tile users")
    @PutMapping("/message")
    @Transactional
    public ResponseEntity<MessageCreateResponse> create(@RequestBody MessageCreateRequest messageCreateRequest, @CurrentUser TileAuthentication authentication) {
        int count = messageService.createMessages(authentication.getTileId(), messageCreateRequest);

        return ResponseEntity.ok(MessageCreateResponse.builder().messagesCreated(count).build());

    }

}
