package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.Message;
import ch.itds.pbs.portal.dto.ActionMessage;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.MessageService;
import ch.itds.pbs.portal.util.Flash;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/message")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class MessageController {

    private final transient MessageService messageService;
    private final transient MessageSource messageSource;

    public MessageController(MessageService messageService, MessageSource messageSource) {
        this.messageService = messageService;
        this.messageSource = messageSource;
    }

    @RequestMapping("")
    public String index(@RequestParam(required = false) Long tileId, @CurrentUser UserPrincipal userPrincipal, Model model) {
        List<Message> messageList = messageService.listMessages(userPrincipal);
        List<MasterTile> masterTileList = messageList.stream().map(m -> m.getUserTile().getMasterTile()).distinct().collect(Collectors.toList());
        model.addAttribute("entityList", messageList);
        model.addAttribute("masterTileList", masterTileList);
        model.addAttribute("tileId", tileId);
        return "message/index";
    }

    @PostMapping(path = "/setRead/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<ActionMessage> setRead(@PathVariable long id, @CurrentUser UserPrincipal userPrincipal, Locale locale) {

        if (messageService.setMessageRead(userPrincipal, id)) {
            return ResponseEntity.ok(ActionMessage
                    .builder()
                    .message(messageSource.getMessage("message.setRead.success", null, locale))
                    .build());
        } else {
            return ResponseEntity.badRequest().body(ActionMessage
                    .builder()
                    .message(messageSource.getMessage("message.setRead.error", null, locale))
                    .build());
        }
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable long id, @CurrentUser UserPrincipal userPrincipal, RedirectAttributes redirectAttributes, Locale locale) {

        if (messageService.delete(userPrincipal, id)) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("message.delete.success", null, locale));
        } else {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("message.delete.error", null, locale));
        }
        return "redirect:/message";
    }

}
