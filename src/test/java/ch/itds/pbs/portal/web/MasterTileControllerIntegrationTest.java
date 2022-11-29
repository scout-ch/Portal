package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.LocalizedString;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.web.page.admin.MasterTileCreatePage;
import ch.itds.pbs.portal.web.page.admin.MasterTileEditPage;
import ch.itds.pbs.portal.web.page.admin.MasterTileIndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

    @Autowired
    MidataGroupRepository midataGroupRepository;


    @Test
    public void testIndex() throws InterruptedException {


        MasterTileIndexPage indexPage = MasterTileIndexPage.open(seleniumHelper, ensurePbsGroup().getId());

        wait2s.until(ExpectedConditions.elementToBeClickable(indexPage.createLink));


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        MasterTileCreatePage createPage = indexPage.clickOnCreate();

        wait2s.until(ExpectedConditions.urlContains("/masterTile/create"));


        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile/create");
    }

    @Test
    public void testCreate() throws InterruptedException {


        MasterTileCreatePage createPage = MasterTileCreatePage.open(seleniumHelper, ensurePbsGroup().getId());

        wait2s.until(ExpectedConditions.urlContains("/masterTile/create"));

        createPage.acceptPrivacyNotice();
        createPage.requestApiKey();
        createPage.fillForm("TitelN", "ContentN", null, null, null, null);


        createPage.submit();

        wait2s.until(ExpectedConditions.urlMatches("\\/masterTile$"));

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");
    }

    @Test
    public void testEdit() throws InterruptedException {

        MasterTile masterTile = ensurePbsNonRestrictedMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId(), ensurePbsGroup().getId());

        wait2s.until(ExpectedConditions.urlContains("/masterTile/edit/"));

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();
        String filename = System.getProperty("user.dir") + "/src/main/resources/static/favicon/android-icon-36x36.png";
        editPage.fillForm("TitelE1", "ContentE1", null, null, null, filename);

        editPage.submit();

        wait2s.until(ExpectedConditions.urlMatches("\\/masterTile$"));

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("android-icon-36x36.png", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testEditAnotherFile() throws InterruptedException {

        MasterTile masterTile = ensurePbsNonRestrictedMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId(), ensurePbsGroup().getId());

        wait2s.until(ExpectedConditions.urlContains("/masterTile/edit/"));

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();

        String filename = System.getProperty("user.dir") + "/src/main/resources/static/favicon/apple-icon-180x180.png";
        editPage.fillForm("TitelE2", "ContentE2", null, null, null, filename);

        editPage.submit();

        wait2s.until(ExpectedConditions.urlMatches("\\/masterTile$"));

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("apple-icon-180x180.png", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testDelete() throws InterruptedException {

        MasterTile masterTile = ensurePbsNonRestrictedMasterTile();
        List<MasterTile> pbsTiles = masterTileRepository.findAllByMidataGroupOnly(ensurePbsGroup());
        for (MasterTile mt : pbsTiles) {
            if ("Titel1".equals(mt.getTitle().getDe())) {
                masterTile = mt;
                break;
            }
        }

        MasterTileIndexPage indexPage = MasterTileIndexPage.open(seleniumHelper, ensurePbsGroup().getId());

        wait2s.until(ExpectedConditions.urlContains("/masterTile"));

        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        indexPage.clickOnDelete(masterTile.getId());

        wait2s.until(seleniumHelper.waitForPageLoad());

        wait2s.until(ExpectedConditions.urlMatches("\\/masterTile$"));

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

        assertThat(masterTileRepository.findById(masterTile.getId())).isEqualTo(Optional.empty());

    }

    /**
     * Test if tile order update by drag & drop works in the webbrowser
     */
    @Test
    public void updatePositionsUsingDragAndDrop() throws InterruptedException {

        Category category = ensureACategory();
        List<MasterTile> tileList = masterTileRepository.findAllByMidataGroupOnly(ensurePbsGroup());
        int c = 1;
        while (tileList.size() < 3) {
            MasterTile tile = new MasterTile();
            tile.setCategory(category);
            tile.setMidataGroupOnly(ensurePbsGroup());
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

        seleniumHelper.navigateTo("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

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

}
