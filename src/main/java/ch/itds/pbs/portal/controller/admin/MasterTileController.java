package ch.itds.pbs.portal.controller.admin;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.util.Flash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.util.Locale;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Controller("masterTileAdminController")
@RequestMapping("/admin/masterTile")
public class MasterTileController {

    private final transient MasterTileRepository masterTileRepository;
    private final transient CategoryRepository categoryRepository;
    private final transient MessageSource messageSource;

    public MasterTileController(MasterTileRepository masterTileRepository, CategoryRepository categoryRepository, MessageSource messageSource) {
        this.masterTileRepository = masterTileRepository;
        this.categoryRepository = categoryRepository;
        this.messageSource = messageSource;
    }

    @GetMapping("")
    public String index(Model model) {

        model.addAttribute("entityList", masterTileRepository.findAllWithCategory());

        return "admin/masterTile/index";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable long id, Model model) {

        MasterTile entity = masterTileRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        model.addAttribute("entity", entity);
        model.addAttribute("categoryList", categoryRepository.findAll());

        return "admin/masterTile/edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable long id, @Validated @ModelAttribute("entity") MasterTile entity, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, Locale locale) {

        MasterTile savedEntity = masterTileRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryList", categoryRepository.findAll());
            return "admin/masterTile/edit";
        }

        BeanUtils.copyProperties(entity, savedEntity, "id", "version", "dateCreated", "lastUpdated");

        try {
            savedEntity = masterTileRepository.save(savedEntity);
        } catch (Exception e) {
            log.error("unable to save entity {}: {}", savedEntity.getClass().getSimpleName(), e.getMessage(), e);
            model.addAttribute(Flash.ERROR, messageSource.getMessage("masterTile.edit.error", new String[]{e.getMessage()}, locale));
            model.addAttribute("categoryList", categoryRepository.findAll());
            return "admin/masterTile/edit";
        }

        redirectAttributes.addFlashAttribute(Flash.SUCCESS, messageSource.getMessage("masterTile.edit.success", null, locale));

        return "redirect:/admin/masterTile";
    }

}
