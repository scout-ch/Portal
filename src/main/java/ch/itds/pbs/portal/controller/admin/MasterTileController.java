package ch.itds.pbs.portal.controller.admin;

import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.FileMeta;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.dto.ActionMessage;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.service.FileService;
import ch.itds.pbs.portal.service.TileService;
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
@PreAuthorize("hasRole('ADMIN')")
@Controller("masterTileAdminController")
@RequestMapping("/admin/masterTile")
public class MasterTileController {

    private final transient MasterTileRepository masterTileRepository;
    private final transient CategoryRepository categoryRepository;
    private final transient MessageSource messageSource;
    private final transient FileService fileService;
    private final transient TileService tileService;

    public MasterTileController(MasterTileRepository masterTileRepository, CategoryRepository categoryRepository, MessageSource messageSource, FileService fileService, TileService tileService) {
        this.masterTileRepository = masterTileRepository;
        this.categoryRepository = categoryRepository;
        this.messageSource = messageSource;
        this.fileService = fileService;
        this.tileService = tileService;
    }

    @GetMapping("")
    public String index(Model model) {

        model.addAttribute("entityList", masterTileRepository.findAllWithCategory());

        return "admin/masterTile/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {

        MasterTile entity = new MasterTile();
        entity.setApiKey(UUID.randomUUID().toString());

        model.addAttribute("entity", entity);
        model.addAttribute("categoryList", categoryRepository.findAll());
        model.addAttribute("colorList", Color.values());

        return "admin/masterTile/edit";
    }

    @PostMapping("/create")
    public String createSave(@Validated @ModelAttribute("entity") MasterTile entity, BindingResult bindingResult,
                             @RequestParam("imageUpload") MultipartFile imageUpload,
                             Model model, RedirectAttributes redirectAttributes, Locale locale) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryList", categoryRepository.findAll());
            model.addAttribute("colorList", Color.values());
            return "admin/masterTile/edit";
        }

        if (imageUpload != null && !imageUpload.isEmpty()) {
            FileMeta image = fileService.upload(imageUpload);
            entity.setImage(image);
        }
        try {
            masterTileRepository.save(entity);
        } catch (Exception e) {
            log.error("unable to save entity {}: {}", entity.getClass().getSimpleName(), e.getMessage(), e);
            model.addAttribute(Flash.ERROR, messageSource.getMessage("masterTile.create.error", new String[]{e.getMessage()}, locale));
            model.addAttribute("categoryList", categoryRepository.findAll());
            model.addAttribute("colorList", Color.values());
            return "admin/masterTile/edit";
        }

        redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.create.success", null, locale));

        return "redirect:/admin/masterTile";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable long id, Model model) {

        MasterTile entity = masterTileRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        model.addAttribute("entity", entity);
        model.addAttribute("categoryList", categoryRepository.findAll());
        model.addAttribute("colorList", Color.values());

        return "admin/masterTile/edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable long id, @Validated @ModelAttribute("entity") MasterTile entity, BindingResult bindingResult,
                           @RequestParam("imageUpload") MultipartFile imageUpload,
                           @RequestParam(required = false, name = "imageUpload-delete") boolean imageDelete,
                           Model model, RedirectAttributes redirectAttributes, Locale locale) {

        MasterTile savedEntity = masterTileRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryList", categoryRepository.findAll());
            model.addAttribute("colorList", Color.values());
            return "admin/masterTile/edit";
        }

        BeanUtils.copyProperties(entity, savedEntity, "id", "version", "dateCreated", "lastUpdated", "image");
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
            model.addAttribute("categoryList", categoryRepository.findAll());
            model.addAttribute("colorList", Color.values());
            return "admin/masterTile/edit";
        }

        redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.edit.success", null, locale));

        return "redirect:/admin/masterTile";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes, Locale locale) {

        MasterTile masterTile = masterTileRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        try {
            masterTileRepository.delete(masterTile);
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.delete.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("masterTile.delete.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/admin/masterTile";
    }

    @PostMapping(path = "/updateSort", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActionMessage> updateSort(@RequestBody Map<Long, Integer> data, Locale locale) {

        List<MasterTile> items = masterTileRepository.findAll();
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

    @RequestMapping("/provisioningAll")
    public String provisioningAll(RedirectAttributes redirectAttributes, Locale locale) {
        try {
            tileService.provisioningAll();
            redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.provisioningAll.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(Flash.ERROR, messageSource.getMessage("masterTile.provisioningAll.error", new String[]{e.getMessage()}, locale));
        }
        return "redirect:/admin/masterTile";
    }

}
