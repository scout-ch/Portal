package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.ActionMessage;
import ch.itds.pbs.portal.dto.ApiKey;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.security.CurrentUser;
import ch.itds.pbs.portal.security.UserPrincipal;
import ch.itds.pbs.portal.service.FileService;
import ch.itds.pbs.portal.service.MidataGroupService;
import ch.itds.pbs.portal.util.Flash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/midataGroup/{midataGroupId:\\d+}/masterTile")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class MasterTileController {

    private final transient MasterTileRepository masterTileRepository;
    private final transient CategoryRepository categoryRepository;
    private final transient MessageSource messageSource;
    private final transient FileService fileService;

    private final transient MidataGroupService midataGroupService;

    public MasterTileController(MasterTileRepository masterTileRepository, CategoryRepository categoryRepository, MessageSource messageSource, FileService fileService, MidataGroupService midataGroupService) {
        this.masterTileRepository = masterTileRepository;
        this.categoryRepository = categoryRepository;
        this.messageSource = messageSource;
        this.fileService = fileService;
        this.midataGroupService = midataGroupService;
    }

    @GetMapping("")
    public String index(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal, Model model) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        model.addAttribute("midataGroup", midataGroup);
        model.addAttribute("entityList", masterTileRepository.findAllByGroupWithCategory(midataGroup));

        return "masterTile/index";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal, Model model) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        MasterTile entity = new MasterTile();
        entity.setMidataGroupOnly(midataGroup);
        entity.setApiKey(UUID.randomUUID().toString());
        Category primaryGroupCategory = midataGroupService.getCategory(midataGroup);
        if (primaryGroupCategory != null) {
            entity.setCategory(primaryGroupCategory);
        }

        model.addAttribute("midataGroup", midataGroup);
        model.addAttribute("entity", entity);
        model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
        model.addAttribute("colorList", Color.values());

        return "masterTile/edit";
    }

    @PostMapping("/create")
    public String createSave(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                             @Validated @ModelAttribute("entity") MasterTile entity, BindingResult bindingResult,
                             @RequestParam("imageUpload") MultipartFile imageUpload,
                             Model model, RedirectAttributes redirectAttributes, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("midataGroup", midataGroup);
            model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
            model.addAttribute("colorList", Color.values());
            return "masterTile/edit";
        }

        if (imageUpload != null && !imageUpload.isEmpty()) {
            FileMeta image = fileService.upload(imageUpload);
            entity.setImage(image);
        }

        entity.setMidataGroupOnly(midataGroup);

        try {
            masterTileRepository.save(entity);
        } catch (Exception e) {
            log.error("unable to save entity {}: {}", entity.getClass().getSimpleName(), e.getMessage(), e);
            model.addAttribute(Flash.ERROR, messageSource.getMessage("masterTile.create.error", new String[]{e.getMessage()}, locale));
            model.addAttribute("midataGroup", midataGroup);
            model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
            model.addAttribute("colorList", Color.values());
            return "masterTile/edit";
        }

        redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.create.success", null, locale));

        return "redirect:/midataGroup/" + midataGroup.getId() + "/masterTile";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal, @PathVariable long id, Model model) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        MasterTile entity = masterTileRepository.findByIdAndMidataGroupOnly(id, midataGroup).orElseThrow(EntityNotFoundException::new);

        model.addAttribute("midataGroup", midataGroup);
        model.addAttribute("entity", entity);
        model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
        model.addAttribute("colorList", Color.values());

        return "masterTile/edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                           @PathVariable long id, @Validated @ModelAttribute("entity") MasterTile entity, BindingResult bindingResult,
                           @RequestParam("imageUpload") MultipartFile imageUpload,
                           @RequestParam(required = false, name = "imageUpload-delete") boolean imageDelete,
                           Model model, RedirectAttributes redirectAttributes, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        MasterTile savedEntity = masterTileRepository.findByIdAndMidataGroupOnly(id, midataGroup).orElseThrow(EntityNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("midataGroup", midataGroup);
            model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
            model.addAttribute("colorList", Color.values());
            return "masterTile/edit";
        }

        BeanUtils.copyProperties(entity, savedEntity, "id", "version", "dateCreated", "lastUpdated", "image", "midataGroupOnly");
        FileMeta imageToDelete = null;

        if (imageUpload != null && !imageUpload.isEmpty()) {
            FileMeta image = fileService.upload(imageUpload);
            if (savedEntity.getImage() != null) {
                imageToDelete = savedEntity.getImage();
            }
            savedEntity.setImage(image);
        }
        if (imageDelete) {
            if (savedEntity.getImage() != null) {
                imageToDelete = savedEntity.getImage();
                savedEntity.setImage(null);
            }
        }

        try {
            savedEntity = masterTileRepository.save(savedEntity);
            if (imageToDelete != null) {
                fileService.delete(imageToDelete);
            }
        } catch (Exception e) {
            log.error("unable to save entity {}: {}", savedEntity.getClass().getSimpleName(), e.getMessage(), e);
            model.addAttribute(Flash.ERROR, messageSource.getMessage("masterTile.edit.error", new String[]{e.getMessage()}, locale));
            model.addAttribute("midataGroup", midataGroup);
            model.addAttribute("categoryList", categoryRepository.findAllForGroup(midataGroup));
            model.addAttribute("colorList", Color.values());
            return "masterTile/edit";
        }

        redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.edit.success", null, locale));

        return "redirect:/midataGroup/" + midataGroup.getId() + "/masterTile";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                         @PathVariable long id, RedirectAttributes redirectAttributes, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        MasterTile masterTile = masterTileRepository.findByIdAndMidataGroupOnly(id, midataGroup).orElseThrow(EntityNotFoundException::new);

        try {
            masterTileRepository.delete(masterTile);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.delete.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("masterTile.delete.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/midataGroup/" + midataGroup.getId() + "/masterTile";
    }

    @PostMapping(path = "/updateSort", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActionMessage> updateSort(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                                                    @RequestBody Map<Long, Integer> data, Locale locale) {

        MidataGroup midataGroup = midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        List<MasterTile> items = masterTileRepository.findAllByMidataGroupOnly(midataGroup);
        for (MasterTile i : items) {
            i.setPosition(data.get(i.getId()));
        }
        masterTileRepository.saveAll(items);

        return ResponseEntity
                .ok(ActionMessage
                        .builder()
                        .message(messageSource.getMessage("masterTile.updateSort.success", null, locale))
                        .build()
                );
    }

    @PostMapping(path = "/generateApiKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiKey> generateApiKey(@PathVariable long midataGroupId, @CurrentUser UserPrincipal userPrincipal,
                                                 Locale locale) {

        midataGroupService.findByIdAndEnsureAdmin(midataGroupId, userPrincipal.getId());

        return ResponseEntity
                .ok(ApiKey
                        .builder()
                        .apiKey(UUID.randomUUID().toString())
                        .message(messageSource.getMessage("masterTile.generateApiKey.success", null, locale))
                        .build()
                );
    }

}
