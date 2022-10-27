package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.LocalizedString;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.Message;
import ch.itds.pbs.portal.dto.MessageCreateRequest;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.MessageRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.CustomUserDetailsService;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.MessageService;
import ch.itds.pbs.portal.service.TileService;
import ch.itds.pbs.portal.web.page.MessagePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageControllerIntegrationTest extends IntegrationTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MasterTileRepository masterTileRepository;

    @Autowired
    TileService tileService;

    @Autowired
    MessageService messageService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserTileRepository userTileRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Creates two messages (service call), set them to read=true, delete the first one
     */
    @Test
    public void testMessageSetReadAndDelete() throws InterruptedException {

        MasterTile tile = ensurePbsGroupDefaultTile().getMasterTile();
        messageRepository.deleteAll();
        userRepository.findAll().forEach(u -> tileService.provisioning(UserPrincipal.create(u)));

        LocalizedString deLanguage = new LocalizedString();
        deLanguage.setDe("Text DE");
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(deLanguage);
        messageCreateRequest.setContent(deLanguage);
        messageCreateRequest.setLimitToUserIds(List.of(3113L));
        messageService.createMessages(tile.getId(), messageCreateRequest);
        messageService.createMessages(tile.getId(), messageCreateRequest);

        Thread.sleep(500);

        MessagePage page = MessagePage.open(seleniumHelper);

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        assertEquals(2, page.getMessageSize());

        page.openMessage(1);

        seleniumHelper.screenshot("message-index-before-delete-0");
        page = page.deleteMessage(0);
        seleniumHelper.screenshot("message-index-after-delete-0");

        assertEquals(1, page.getMessageSize());

        List<Message> messages = messageService.listMessages(UserPrincipal.create(userRepository.findByUsernameWithRoles("3113").get()));

        assertEquals(1, messages.size());
        assertTrue(messages.get(0).isRead());
    }


}
