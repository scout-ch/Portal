package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.GroupDefaultTile;
import ch.itds.pbs.portal.domain.MidataGroup;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.GroupDefaultTileRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.web.page.CatalogPage;
import ch.itds.pbs.portal.web.page.MidataGroupDefaultTilePage;
import ch.itds.pbs.portal.web.page.MidataGroupIndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MidataGroupControllerIntegrationTest extends IntegrationTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MidataGroupRepository midataGroupRepository;

    @Autowired
    GroupDefaultTileRepository groupDefaultTileRepository;

    @Test
    public void testIndex() {


        MidataGroupIndexPage midataGroupIndexPage = MidataGroupIndexPage.open(seleniumHelper);

        seleniumHelper.screenshot("index");

        midataGroupIndexPage.acceptPrivacyNotice();

        assertTrue(midataGroupIndexPage.getTiles().size() > 0);
    }

    @Test
    public void testAddDefaultFromCatalog() {


        MidataGroupIndexPage midataGroupIndexPage = MidataGroupIndexPage.open(seleniumHelper);

        midataGroupIndexPage.acceptPrivacyNotice();

        MidataGroupIndexPage.MidataGroupRow row = midataGroupIndexPage.getTiles().get(0);

        long groupId = Long.parseLong(row.getElement().getAttribute("id").split("group")[1]);

        MidataGroup group = midataGroupRepository.findById(groupId).orElse(null);

        row.getDefaultTileBtn().click();

        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Kachel aus Katalog hinzufügen")));

        MidataGroupDefaultTilePage defaultTilePage = MidataGroupDefaultTilePage.openExisting(seleniumHelper);

        int initialTileCount = groupDefaultTileRepository.findAllByGroup(group).size();

        defaultTilePage.getAddTileBtn().click();

        CatalogPage catalogPage = CatalogPage.openExisting(seleniumHelper);

        seleniumHelper.screenshot("catalog");

        CatalogPage.CatalogTile tile = catalogPage.getTiles().get(0);
        tile.getAddBtn().click();


        wait2s.until(seleniumHelper.waitForPageLoad());

        seleniumHelper.screenshot("added");

        int afterAddTileCount = groupDefaultTileRepository.findAllByGroup(group).size();

        assertEquals(initialTileCount + 1, afterAddTileCount);
    }

    @Test
    public void testDelete() {

        WebDriverWait wait2sPolling10ms = new WebDriverWait(seleniumHelper.getDriver(), Duration.ofSeconds(2), Duration.ofMillis(10));

        MidataGroup group = ensurePbsGroup();
        GroupDefaultTile pkbTile = ensureGroupTile(group, ensurePkbNonRestrictedMasterTile());

        MidataGroupDefaultTilePage defaultTilePage = MidataGroupDefaultTilePage.open(seleniumHelper, group.getId());

        defaultTilePage.acceptPrivacyNotice();


        int initialTileCount = groupDefaultTileRepository.findAllByGroup(group).size();


        List<MidataGroupDefaultTilePage.GroupDefaultTileRow> data = defaultTilePage.getTiles();

        data.get(0).getDeleteBtn().click();
        seleniumHelper.getDriver().switchTo().alert().accept();

        wait2sPolling10ms.until(seleniumHelper.waitForReadyState("loading"));
        wait2s.until(seleniumHelper.waitForReadyState("complete"));

        seleniumHelper.screenshot("deleted");

        int afterDeleteTileCount = groupDefaultTileRepository.findAllByGroup(group).size();
        assertEquals(initialTileCount - 1, afterDeleteTileCount);

    }

    /**
     * Test if tile order update by drag & drop works in the webbrowser
     */
    @Test
    public void updatePositionsUsingDragAndDrop() throws InterruptedException {

        MidataGroup group = ensurePbsGroup();
        GroupDefaultTile pbsTile = ensureGroupTile(group, ensurePbsNonRestrictedMasterTile());
        GroupDefaultTile pkbTile = ensureGroupTile(group, ensurePkbNonRestrictedMasterTile());

        int oldPbsPos = pbsTile.getPosition();
        int oldPkbPos = pkbTile.getPosition();

        MidataGroupDefaultTilePage defaultTilePage = MidataGroupDefaultTilePage.open(seleniumHelper, group.getId());

        defaultTilePage.acceptPrivacyNotice();

        List<MidataGroupDefaultTilePage.GroupDefaultTileRow> data = defaultTilePage.getTiles();

        assertEquals(2, data.size());

        WebElement from = data.get(1).getGripHandle();
        WebElement to = data.get(0).getGripHandle();

        Actions act = new Actions(seleniumHelper.getDriver());

        seleniumHelper.screenshot("start");

        act.moveToElement(from).clickAndHold().moveToElement(to).release().perform();

        seleniumHelper.screenshot("move");

        Thread.sleep(250);

        seleniumHelper.screenshot("end");

        pbsTile = groupDefaultTileRepository.findById(pbsTile.getId()).get();
        pkbTile = groupDefaultTileRepository.findById(pkbTile.getId()).get();

        int newPbsPos = pbsTile.getPosition();
        int newPkbPos = pkbTile.getPosition();

        assertEquals(1, oldPkbPos - oldPbsPos);
        assertEquals(1, newPbsPos - newPkbPos);

    }

}
