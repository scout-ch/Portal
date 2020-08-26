package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Message;
import ch.itds.pbs.portal.repo.MessageRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final transient MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> listMessages(UserPrincipal userPrincipal) {
        return messageRepository.findAllCompleteByUserId(userPrincipal.getId());
    }

    public int countUnreadMessages(UserPrincipal userPrincipal) {
        return messageRepository.countUnreadMessages(userPrincipal.getId());
    }
}
