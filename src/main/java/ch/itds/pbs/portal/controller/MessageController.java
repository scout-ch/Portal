package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class MessageController {

    private final transient MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("")
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model) {
        model.addAttribute("entityList", messageService.listMessages(userPrincipal));
        return "message/index";
    }

}
