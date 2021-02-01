package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.LocalizedString;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.web.page.admin.MasterTileCreatePage;
import ch.itds.pbs.portal.web.page.admin.MasterTileEditPage;
import ch.itds.pbs.portal.web.page.admin.MasterTileIndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MasterTileControllerIntegrationTest extends IntegrationTest {

    @Autowired
    MasterTileRepository masterTileRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void testIndex() throws InterruptedException {


        MasterTileIndexPage indexPage = MasterTileIndexPage.open(seleniumHelper);

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        MasterTileCreatePage createPage = indexPage.clickOnCreate();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/masterTile/create");
    }

    @Test
    public void testCreate() throws InterruptedException {


        MasterTileCreatePage createPage = MasterTileCreatePage.open(seleniumHelper);

        Thread.sleep(1500);

        createPage.acceptPrivacyNotice();
        createPage.requestApiKey();
        createPage.fillForm("Titel1", "Content1", null, null, null, null);

        Thread.sleep(1500);

        createPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/masterTile");
    }

    @Test
    public void testEdit() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId());

        Thread.sleep(1500);

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();
        editPage.fillForm("Titel1", "Content1", null, null, null, "/etc/hosts");

        Thread.sleep(1500);

        editPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("hosts", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testEditAnotherFile() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId());

        Thread.sleep(1500);

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();
        editPage.fillForm("Titel1", "Content1", null, null, null, "/etc/hostname");

        Thread.sleep(1500);

        editPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("hostname", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testDelete() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();

        MasterTileIndexPage indexPage = MasterTileIndexPage.open(seleniumHelper);

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        indexPage.clickOnDelete(masterTile.getId());

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/masterTile");

        assertThat(masterTileRepository.findById(masterTile.getId())).isEqualTo(Optional.empty());

    }

    /**
     * Test if tile order update by drag & drop works in the webbrowser
     */
    @Test
    public void updatePositionsUsingDragAndDrop() throws InterruptedException {

        Category category = ensureACategory();
        List<MasterTile> tileList = masterTileRepository.findAll();
        int c = 1;
        while (tileList.size() < 3) {
            MasterTile tile = new MasterTile();
            tile.setCategory(category);
            LocalizedString t = new LocalizedString();
            t.setDe("Tile " + c);
            tile.setTitle(t);
            LocalizedString cont = new LocalizedString();
            cont.setDe("Content " + c);
            tile.setContent(cont);
            tile.setPosition(c);
            masterTileRepository.save(tile);
            tileList = masterTileRepository.findAll();
            c++;
        }
        for (MasterTile p : tileList) {
            log.info("Tile {} @ {}: {}", p.getId(), p.getPosition(), p.getTitle().getDe());
        }

        seleniumHelper.navigateTo("/admin/masterTile");

        seleniumHelper.screenshot("start");


        List<WebElement> elementList = seleniumHelper.getDriver().findElements(By.cssSelector(".table-draggable .grip-handle"));

        WebElement from = elementList.get(0);
        WebElement to = elementList.get(2);

        long newSecondId = Long.parseLong(from.findElement(By.xpath("./../..")).getAttribute("data-id"));

        Actions act = new Actions(seleniumHelper.getDriver());

        act.moveToElement(from).clickAndHold().moveToElement(to).release().perform();

        Thread.sleep(250);

        seleniumHelper.screenshot("end");


        tileList = masterTileRepository.findAll();
        Integer newSecondPosition = null;
        for (MasterTile tile : tileList) {
            log.info("Tile {} @ {}: {}", tile.getId(), tile.getPosition(), tile.getTitle().getDe());
            if (tile.getId().equals(newSecondId)) {
                newSecondPosition = tile.getPosition();
            }
        }
        assertThat(newSecondPosition).isEqualTo(1);

    }

    private MasterTile ensureMasterTile() {
        List<MasterTile> masterTiles = masterTileRepository.findAll();
        MasterTile masterTile;
        if (masterTiles.isEmpty()) {
            Category category = ensureACategory();

            LocalizedString title = new LocalizedString();
            title.setDe("Titel");
            LocalizedString content = new LocalizedString();
            content.setDe("Content...");

            masterTile = new MasterTile();
            masterTile.setCategory(category);
            masterTile.setTitle(title);
            masterTile.setContent(content);
            masterTile.setBackgroundColor(Color.DEFAULT);
            masterTile = masterTileRepository.saveAndFlush(masterTile);
        } else {
            masterTile = masterTiles.get(0);
        }
        return masterTile;
    }

    private Category ensureACategory() {
        Category category;
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            LocalizedString categoryName = new LocalizedString();
            categoryName.setDe("Kategorie");

            category = new Category();
            category.setName(categoryName);
            category = categoryRepository.save(category);
        } else {
            category = categories.get(0);
        }
        return category;
    }

}
