package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.dto.LocalizedTile;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.LanguageService;
import ch.itds.pbs.portal.service.TileService;
import ch.itds.pbs.portal.util.Flash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Slf4j
@Controller
@RequestMapping("/")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class IndexController {

    private final transient TileService tileService;
    private final transient LanguageService languageService;

    private final transient MessageSource messageSource;

    public IndexController(TileService tileService, LanguageService languageService, MessageSource messageSource) {
        this.tileService = tileService;
        this.languageService = languageService;
        this.messageSource = messageSource;
    }

    @RequestMapping("")
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model, Locale locale) {

        Language language = languageService.convertToLanguage(locale);

        List<LocalizedTile> tiles = tileService.listTiles(userPrincipal, language);
        if (tiles.isEmpty()) {
            tileService.provisioning(userPrincipal);
            tiles = tileService.listTiles(userPrincipal, language);
        }

        model.addAttribute("tiles", tiles);

        return "index";
    }

    @PostMapping("/provisioning")
    public String provisioning(@CurrentUser UserPrincipal userPrincipal, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            tileService.provisioning(userPrincipal);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.provisioning.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("masterTile.provisioning.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/";
    }

}
