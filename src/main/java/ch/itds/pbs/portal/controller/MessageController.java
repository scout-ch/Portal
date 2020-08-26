package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.MessageService;
import ch.itds.pbs.portal.util.Flash;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

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
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model) {
        model.addAttribute("entityList", messageService.listMessages(userPrincipal));
        return "message/index";
    }

    @RequestMapping("/setRead/{id}")
    @Transactional
    public ResponseEntity<?> setRead(@PathVariable long id, @CurrentUser UserPrincipal userPrincipal) {

        if (messageService.setMessageRead(userPrincipal, id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping("/delete/{id}")
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
