package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.GroupDefaultTile;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.dto.ActionMessage;
import ch.itds.pbs.portal.repo.GroupDefaultTileRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.MidataGroupService;
import ch.itds.pbs.portal.util.Flash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/midataGroup")
public class MidataGroupController {


    private final MidataGroupService midataGroupService;
    private final MidataGroupRepository midataGroupRepository;

    private final MasterTileRepository masterTileRepository;

    private final MessageSource messageSource;

    private final GroupDefaultTileRepository groupDefaultTileRepository;

    public MidataGroupController(MidataGroupService midataGroupService, MidataGroupRepository midataGroupRepository, MasterTileRepository masterTileRepository, MessageSource messageSource, GroupDefaultTileRepository groupDefaultTileRepository) {
        this.midataGroupService = midataGroupService;
        this.midataGroupRepository = midataGroupRepository;
        this.masterTileRepository = masterTileRepository;
        this.messageSource = messageSource;
        this.groupDefaultTileRepository = groupDefaultTileRepository;
    }

    @RequestMapping("")
    public String index(@CurrentUser UserPrincipal userPrincipal, Model model) {

        List<MidataGroup> groups = midataGroupRepository.findAllWithAdminPermission(userPrincipal.getId());
        model.addAttribute("groups", groups);

        return "midataGroup/index";
    }

    @RequestMapping("/{midataGroupId:\\d+}/defaultTile")
    public String index(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal, Model model) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());
        model.addAttribute("midataGroup", midataGroup);
        model.addAttribute("groupDefaultTiles", groupDefaultTileRepository.findAllEnabledWithFetchForUserId(midataGroup.getId()));

        return "midataGroup/defaultTile";
    }

    @PostMapping("/{midataGroupId:\\d+}/defaultTile/create/{masterTileId:\\d+}")
    public String defaultTileAdd(@PathVariable long midataGroupId, @PathVariable long masterTileId,
                                 @CurrentUser UserPrincipal userPrincipal,
                                 RedirectAttributes redirectAttributes, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());
        String redirectToGroupUrl = "redirect:/midataGroup/" + midataGroup.getId() + "/defaultTile";

        MasterTile masterTile = masterTileRepository.findEnabledWithFetchForUserByIdAndGroupsOrNotRestricted(masterTileId, userPrincipal.getId()).orElse(null);

        if (masterTile == null) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("midataGroup.defaultTile.create.error.notFound", null, locale));
            return redirectToGroupUrl;
        }

        if (!groupDefaultTileRepository.findAllByGroupAndMasterTile(midataGroup, masterTile).isEmpty()) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("midataGroup.defaultTile.create.error.alreadyExists", null, locale));
            return redirectToGroupUrl;
        }

        GroupDefaultTile groupDefaultTile = new GroupDefaultTile();
        groupDefaultTile.setGroup(midataGroup);
        groupDefaultTile.setMasterTile(masterTile);
        groupDefaultTile.setPosition(masterTile.getPosition());

        try {
            groupDefaultTileRepository.save(groupDefaultTile);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("midataGroup.defaultTile.create.success", null, locale));
        } catch (Exception e) {
            log.error("unable to add groupDefaultTile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("midataGroup.defaultTile.create.error", null, locale));
        }

        return redirectToGroupUrl;
    }

    @PostMapping("/{midataGroupId:\\d+}/defaultTile/delete/{id}")
    @Transactional
    public String delete(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                         @PathVariable long id, RedirectAttributes redirectAttributes, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        GroupDefaultTile groupDefaultTile = groupDefaultTileRepository.findByIdAndGroup(id, midataGroup).orElseThrow(EntityNotFoundException::new);

        try {
            groupDefaultTileRepository.delete(groupDefaultTile);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("midataGroup.defaultTile.delete.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("midataGroup.defaultTile.delete.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/midataGroup/" + midataGroup.getId() + "/defaultTile";
    }

    @PostMapping(path = "/{midataGroupId:\\d+}/updateSort", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActionMessage> updateSort(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                                                    @RequestBody Map<Long, Integer> data, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());


        List<GroupDefaultTile> items = groupDefaultTileRepository.findAllByGroup(midataGroup);
        for (GroupDefaultTile i : items) {
            i.setPosition(data.get(i.getId()));
        }
        groupDefaultTileRepository.saveAll(items);

        return ResponseEntity
                .ok(ActionMessage
                        .builder()
                        .message(messageSource.getMessage("masterTile.updateSort.success", null, locale))
                        .build()
                );
    }

}
