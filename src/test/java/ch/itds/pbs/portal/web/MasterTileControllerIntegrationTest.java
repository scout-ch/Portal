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

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        MasterTileCreatePage createPage = indexPage.clickOnCreate();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile/create");
    }

    @Test
    public void testCreate() throws InterruptedException {


        MasterTileCreatePage createPage = MasterTileCreatePage.open(seleniumHelper, ensurePbsGroup().getId());

        Thread.sleep(1500);

        createPage.acceptPrivacyNotice();
        createPage.requestApiKey();
        createPage.fillForm("Titel1", "Content1", null, null, null, null);

        Thread.sleep(1500);

        createPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");
    }

    @Test
    public void testEdit() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId(), ensurePbsGroup().getId());

        Thread.sleep(1500);

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();
        editPage.fillForm("Titel1", "Content1", null, null, null, "/etc/hosts");

        Thread.sleep(1500);

        editPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("hosts", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testEditAnotherFile() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();


        MasterTileEditPage editPage = MasterTileEditPage.open(seleniumHelper, masterTile.getId(), ensurePbsGroup().getId());

        Thread.sleep(1500);

        editPage.acceptPrivacyNotice();
        editPage.requestApiKey();
        editPage.fillForm("Titel1", "Content1", null, null, null, "/etc/hostname");

        Thread.sleep(1500);

        editPage.submit();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/admin/midataGroup/" + ensurePbsGroup().getId() + "/masterTile");

        MasterTile updatedMasterTile = masterTileRepository.findById(masterTile.getId()).get();
        assertEquals("hostname", updatedMasterTile.getImage().getName());
    }

    @Test
    public void testDelete() throws InterruptedException {

        MasterTile masterTile = ensureMasterTile();

        MasterTileIndexPage indexPage = MasterTileIndexPage.open(seleniumHelper, ensurePbsGroup().getId());

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        indexPage.clickOnDelete(masterTile.getId());

        Thread.sleep(1500);

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
