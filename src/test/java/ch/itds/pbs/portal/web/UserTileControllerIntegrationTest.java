package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.domain.UserTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.web.page.IndexPage;
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

public class UserTileControllerIntegrationTest extends IntegrationTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MidataGroupRepository midataGroupRepository;

    @Test
    public void testIndex() {


        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Kachel hinzufügen")));


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        seleniumHelper.screenshot("index");

        final WebElement customerNav = seleniumHelper.getDriver().findElement(By.partialLinkText("Kachel hinzufügen"));
        customerNav.click();

        wait2s.until(seleniumHelper.waitForPageLoad());

        seleniumHelper.screenshot("catalog");
    }

    @Test
    public void testApplyShareNew() {

        User user = userRepository.findByUsername("3113").get();

        MasterTile masterTile = ensurePbsNonRestrictedMasterTile();

        // ensure user tile does not yet exist
        UserTile existingTile = ensureUserTile(user, masterTile);
        userTileRepository.delete(existingTile);

        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Kachel hinzufügen")));

        int initialTileCount = userTileRepository.findAllByUser(user).size();


        seleniumHelper.navigateTo("/share/tile/" + masterTile.getId());
        wait2s.until(seleniumHelper.waitForPageLoad());

        seleniumHelper.screenshot("question");

        final WebElement customerNav = seleniumHelper.getDriver().findElement(By.cssSelector("button[type='submit']"));
        customerNav.click();

        wait2s.until(seleniumHelper.waitForPageLoad());

        seleniumHelper.screenshot("added");

        int afterAddTileCount = userTileRepository.findAllByUser(user).size();
        assertEquals(initialTileCount + 1, afterAddTileCount);
    }

    @Test
    public void testApplyShareAlreadyExists() {

        User user = userRepository.findByUsername("3113").get();

        MasterTile masterTile = ensurePbsNonRestrictedMasterTile();

        // ensure user tile does exist
        UserTile existingTile = ensureUserTile(user, masterTile);

        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Kachel hinzufügen")));

        int initialTileCount = userTileRepository.findAllByUser(user).size();


        seleniumHelper.navigateTo("/share/tile/" + masterTile.getId());

        wait2s.until(seleniumHelper.waitForPageLoad());

        seleniumHelper.getDriver().getCurrentUrl().matches(".*:[0-9]*/$");
        seleniumHelper.screenshot("not-added");

        int afterNotAddTileCount = userTileRepository.findAllByUser(user).size();
        assertEquals(initialTileCount, afterNotAddTileCount);
    }

    @Test
    public void testDelete() {

        WebDriverWait wait2sPolling10ms = new WebDriverWait(seleniumHelper.getDriver(), Duration.ofSeconds(2), Duration.ofMillis(10));

        User user = userRepository.findByUsername("3113").get();
        ensureUserTile(user, ensurePbsNonRestrictedMasterTile());
        ensureUserTile(user, ensureBezOeRestrictedMasterTile());

        IndexPage dashboardPage = IndexPage.open(seleniumHelper);


        int initialTileCount = userTileRepository.findAllByUser(user).size();

        dashboardPage.acceptPrivacyNotice();

        dashboardPage.getToggleTileEditorBtn().click();
        seleniumHelper.screenshot("edit active");

        List<IndexPage.IndexTile> data = dashboardPage.getTiles();

        data.get(0).getDeleteBtn().click();
        seleniumHelper.getDriver().switchTo().alert().accept();

        wait2sPolling10ms.until(seleniumHelper.waitForReadyState("loading"));
        wait2s.until(seleniumHelper.waitForReadyState("complete"));

        seleniumHelper.screenshot("deleted");

        int afterDeleteTileCount = userTileRepository.findAllByUser(user).size();
        assertEquals(initialTileCount - 1, afterDeleteTileCount);

    }

    /**
     * Test if tile order update by drag & drop works in the webbrowser
     */
    @Test
    public void updatePositionsUsingDragAndDrop() throws InterruptedException {

        User user = userRepository.findByUsername("3113").get();
        UserTile pbsTile = ensureUserTile(user, ensurePbsNonRestrictedMasterTile());
        UserTile pkbTile = ensureUserTile(user, ensurePkbNonRestrictedMasterTile());

        int oldPbsPos = pbsTile.getPosition();
        int oldPkbPos = pkbTile.getPosition();

        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        dashboardPage.acceptPrivacyNotice();

        dashboardPage.getToggleTileEditorBtn().click();
        seleniumHelper.screenshot("edit active");

        List<IndexPage.IndexTile> data = dashboardPage.getTiles();

        assertEquals(2, data.size());

        WebElement from = data.get(0).getMoveBtn();
        WebElement to = data.get(1).getMoveBtn();

        Actions act = new Actions(seleniumHelper.getDriver());

        seleniumHelper.screenshot("start");

        act.moveToElement(from).clickAndHold().moveToElement(to).release().perform();

        seleniumHelper.screenshot("move");

        Thread.sleep(250);

        seleniumHelper.screenshot("end");

        pbsTile = userTileRepository.findById(pbsTile.getId()).get();
        pkbTile = userTileRepository.findById(pkbTile.getId()).get();

        int newPbsPos = pbsTile.getPosition();
        int newPkbPos = pkbTile.getPosition();

        assertEquals(1, oldPkbPos - oldPbsPos);
        assertEquals(1, newPbsPos - newPkbPos);

    }

}
