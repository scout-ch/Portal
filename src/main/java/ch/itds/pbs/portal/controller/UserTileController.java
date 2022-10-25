package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.util.Locale;

@Slf4j
@Controller
@RequestMapping("/userTile")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class UserTileController {

    private final transient TileService tileService;
    private final transient LanguageService languageService;

    private final transient MessageSource messageSource;

    private final transient MasterTileRepository masterTileRepository;

    private final UserTileRepository userTileRepository;

    private final UserRepository userRepository;

    public UserTileController(TileService tileService, LanguageService languageService, MessageSource messageSource, MasterTileRepository masterTileRepository, UserTileRepository userTileRepository, UserRepository userRepository) {
        this.tileService = tileService;
        this.languageService = languageService;
        this.messageSource = messageSource;
        this.masterTileRepository = masterTileRepository;
        this.userTileRepository = userTileRepository;
        this.userRepository = userRepository;
    }


    @PostMapping("/provisioning")
    public String provisioning(@CurrentUser UserPrincipal userPrincipal, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            tileService.provisioning(userPrincipal);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("userTile.provisioning.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.provisioning.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/";
    }

    @GetMapping("/create/{masterTileId:\\d+}")
    public String userTileCreateAsk(@PathVariable long masterTileId,
                                    @CurrentUser UserPrincipal userPrincipal, Model model,
                                    RedirectAttributes redirectAttributes, Locale locale) {


        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(EntityNotFoundException::new);
        Language language = languageService.convertToLanguage(locale);
        MasterTile masterTile = masterTileRepository.findEnabledWithFetchForUserByIdAndGroups(masterTileId, userPrincipal.getId()).orElse(null);

        if (masterTile == null) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.create.error.notFound", null, locale));
            return "redirect:/";
        }

        if (!userTileRepository.findAllByUserAndMasterTile(user, masterTile).isEmpty()) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.create.error.alreadyExists", null, locale));
            return "redirect:/";
        }

        model.addAttribute("masterTile", masterTile);
        model.addAttribute("localizedTile", tileService.convertToLocalized(masterTile, language));

        return "/userTile/create";
    }

    @PostMapping("/create/{masterTileId:\\d+}")
    public String userTileCreate(@PathVariable long masterTileId,
                                 @CurrentUser UserPrincipal userPrincipal,
                                 RedirectAttributes redirectAttributes, Locale locale) {


        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(EntityNotFoundException::new);

        MasterTile masterTile = masterTileRepository.findEnabledWithFetchForUserByIdAndGroups(masterTileId, userPrincipal.getId()).orElse(null);

        if (masterTile == null) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.create.error.notFound", null, locale));
            return "redirect:/";
        }

        if (!userTileRepository.findAllByUserAndMasterTile(user, masterTile).isEmpty()) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.create.error.alreadyExists", null, locale));
            return "redirect:/";
        }

        UserTile groupDefaultTile = new UserTile();
        groupDefaultTile.setUser(user);
        groupDefaultTile.setMasterTile(masterTile);
        groupDefaultTile.setPosition(masterTile.getPosition());

        try {
            userTileRepository.save(groupDefaultTile);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("userTile.create.success", null, locale));
        } catch (Exception e) {
            log.error("unable to add userTile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("userTile.create.error", null, locale));
        }

        return "redirect:/";
    }

}
