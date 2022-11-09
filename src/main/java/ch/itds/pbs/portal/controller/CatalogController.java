package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.dto.LocalizedTile;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.LanguageService;
import ch.itds.pbs.portal.service.MidataGroupService;
import ch.itds.pbs.portal.service.TileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/catalog")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class CatalogController {

    private final LanguageService languageService;
    private final TileService tileService;

    private final MidataGroupService midataGroupService;

    public CatalogController(LanguageService languageService, TileService tileService, MidataGroupService midataGroupService) {
        this.languageService = languageService;
        this.tileService = tileService;
        this.midataGroupService = midataGroupService;
    }

    @RequestMapping("")
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model, Locale locale,
                        @RequestParam Optional<Long> selectForMidataGroupId,
                        @RequestParam Optional<String> filterTitle, @RequestParam Optional<Long> filterCategory
    ) {

        Language language = languageService.convertToLanguage(locale);

        List<LocalizedTile> tiles = tileService.listTilesByGroupsOrNotRestricted(userPrincipal, language);

        if (selectForMidataGroupId.isPresent()) {
            MidataGroup selectForMidataGroup = midataGroupService.findByIdAndEnsureAdmin(selectForMidataGroupId.get(), userPrincipal.getId());
            model.addAttribute("selectForMidataGroup", selectForMidataGroup);
        }

        Map<Long, String> categories = new HashMap<>();
        for (LocalizedTile tile : tiles) {
            if (!categories.containsKey(tile.getCategoryId())) {
                categories.put(tile.getCategoryId(), tile.getCategory());
            }
        }

        model.addAttribute("tiles", tiles);
        model.addAttribute("categories", categories);
        filterTitle.ifPresent(title -> {
            model.addAttribute("filterTitle", title);
        });
        filterCategory.ifPresent(category -> {
            model.addAttribute("filterCategory", category);
        });

        return "catalog/index";
    }

}
