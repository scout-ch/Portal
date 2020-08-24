package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.TileService;
import ch.itds.pbs.portal.util.Flash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
@PreAuthorize("isAuthenticated()")
public class IndexController {

    private final transient TileService tileService;

    public IndexController(TileService tileService) {
        this.tileService = tileService;
    }

    @RequestMapping("")
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model) {

        log.debug("user: {}", userPrincipal.getUsername());

        tileService.init();

        List<MasterTile> tiles = tileService.listTiles(userPrincipal);
        if (tiles.isEmpty()) {
            tileService.provisioning(userPrincipal);
            tileService.listTiles(userPrincipal);
        }

        model.addAttribute("tiles", tiles);
        model.addAttribute(Flash.INFO, "Info!");

        return "index";
    }

}
