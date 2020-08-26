package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.Message;
import ch.itds.pbs.portal.domain.UserTile;
import ch.itds.pbs.portal.dto.MessageCreateRequest;
import ch.itds.pbs.portal.repo.MessageRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private final transient UserTileRepository userTileRepository;
    private final transient MessageRepository messageRepository;

    public MessageService(UserTileRepository userTileRepository, MessageRepository messageRepository) {
        this.userTileRepository = userTileRepository;
        this.messageRepository = messageRepository;
    }

    public List<Message> listMessages(UserPrincipal userPrincipal) {
        return messageRepository.findAllCompleteByUserId(userPrincipal.getId());
    }

    public int countUnreadMessages(UserPrincipal userPrincipal) {
        return messageRepository.countUnreadMessages(userPrincipal.getId());
    }

    public int createMessages(Long tileId, MessageCreateRequest messageCreateRequest) {

        List<UserTile> targets;
        if (messageCreateRequest.getLimitToUserIds() == null || messageCreateRequest.getLimitToUserIds().isEmpty()) {
            targets = userTileRepository.findAllByMasterTileIdWithUser(tileId);
        } else {
            targets = userTileRepository.findAllByMasterTileIdAndUserMidataIdWithUser(tileId, messageCreateRequest.getLimitToUserIds());
        }

        List<Message> messages = new ArrayList<>();

        for (UserTile ut : targets) {
            Language language = ut.getUser().getLanguage();
            String title = messageCreateRequest.getTitle().getOrDefault(language, null);
            String content = messageCreateRequest.getContent().getOrDefault(language, null);
            String url = messageCreateRequest.getUrl().getOrDefault(language, null);

            if (!Strings.isEmpty(title) && !Strings.isEmpty(content)) {
                Message m = new Message();
                m.setUserTile(ut);
                m.setTitle(title);
                m.setContent(content);
                m.setUrl(url);
                m.setRead(false);
                messages.add(m);
            }
        }

        messageRepository.saveAll(messages);

        return messages.size();
    }

    public boolean setMessageRead(UserPrincipal userPrincipal, long id) {
        return messageRepository.setRead(userPrincipal.getId(), id) == 1;
    }

    public boolean delete(UserPrincipal userPrincipal, long id) {
        return messageRepository.delete(userPrincipal.getId(), id) == 1;
    }
}
