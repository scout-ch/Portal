package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.LocalizedString;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;
import ch.itds.pbs.portal.dto.MessageCreateRequest;
import ch.itds.pbs.portal.repo.MessageRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    MessageService messageService;

    @MockBean
    UserTileRepository userTileRepository;

    @MockBean
    MessageRepository messageRepository;

    LocalizedString deLanguage;

    LocalizedString frLanguage;

    LocalizedString allLanguages;

    LocalizedString noLanguages;

    @BeforeEach
    public void initMocks() {
        UserTile ut0 = new UserTile();
        User user0 = new User();
        user0.setLanguage(Language.DE);
        ut0.setUser(user0);

        UserTile ut1 = new UserTile();
        User user1 = new User();
        user1.setLanguage(Language.FR);
        ut1.setUser(user1);

        UserTile ut2 = new UserTile();
        User user2 = new User();
        user2.setLanguage(Language.EN);
        ut2.setUser(user2);

        Mockito.when(userTileRepository.findAllByMasterTileIdAndUserMidataIdWithUser(any(), any())).thenReturn(List.of(ut0, ut1, ut2));

        deLanguage = new LocalizedString();
        deLanguage.setDe("Text DE");

        frLanguage = new LocalizedString();
        frLanguage.setFr("Text FR");

        allLanguages = new LocalizedString();
        allLanguages.setDe("Text DE");
        allLanguages.setFr("Text FR");
        allLanguages.setIt("Text IT");
        allLanguages.setEn("Text EN");

        noLanguages = new LocalizedString();

        Mockito.when(messageRepository.setRead(anyLong(), anyLong())).thenReturn(1);
        Mockito.when(messageRepository.delete(anyLong(), anyLong())).thenReturn(1);
    }

    @Test
    public void messagesForCompleteMessageCreateRequest() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(allLanguages);
        messageCreateRequest.setContent(allLanguages);
        messageCreateRequest.setLimitToUserIds(List.of(1L));
        int msgCount = messageService.createMessages(1L, messageCreateRequest);
        assertEquals(3, msgCount);
    }

    @Test
    public void messagesForOneLanguageMessageCreateRequest() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(deLanguage);
        messageCreateRequest.setContent(deLanguage);
        messageCreateRequest.setLimitToUserIds(List.of(1L));
        int msgCount = messageService.createMessages(1L, messageCreateRequest);
        assertEquals(1, msgCount);
    }

    @Test
    public void noMessagesForEmptyMessageCreateRequest() {
        MessageCreateRequest emptyMessageCreateRequest = new MessageCreateRequest();
        int msgCount = messageService.createMessages(1L, emptyMessageCreateRequest);
        assertEquals(0, msgCount);
    }

    @Test
    public void noMessagesForMissingUserLimitMessageCreateRequest() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(deLanguage);
        messageCreateRequest.setContent(deLanguage);
        int msgCount = messageService.createMessages(1L, messageCreateRequest);
        assertEquals(0, msgCount);
    }

    @Test
    public void noMessagesForEmptyUserLimitMessageCreateRequest() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(deLanguage);
        messageCreateRequest.setContent(deLanguage);
        messageCreateRequest.setLimitToUserIds(new ArrayList<>());
        int msgCount = messageService.createMessages(1L, messageCreateRequest);
        assertEquals(0, msgCount);
    }

    @Test
    public void noMessagesForInvalidLanguageMessageCreateRequest() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setTitle(deLanguage);
        messageCreateRequest.setContent(frLanguage);
        messageCreateRequest.setLimitToUserIds(List.of(1L));
        int msgCount = messageService.createMessages(1L, messageCreateRequest);
        assertEquals(0, msgCount);
    }

    @Test
    public void setRead() {
        User u = new User();
        u.setId(1L);
        u.setLanguage(Language.DE);
        u.setRoles(new HashSet<>());
        UserPrincipal up = UserPrincipal.create(u);
        assertTrue(messageService.setMessageRead(up, 1L));
    }

    @Test
    public void delete() {
        User u = new User();
        u.setId(1L);
        u.setLanguage(Language.DE);
        u.setRoles(new HashSet<>());
        UserPrincipal up = UserPrincipal.create(u);
        assertTrue(messageService.delete(up, 1L));
    }

}