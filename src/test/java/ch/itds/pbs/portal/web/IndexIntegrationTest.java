package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.web.page.IndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexIntegrationTest extends IntegrationTest {
    @Test
    public void testIndex() throws InterruptedException {


        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Nachrichten")));


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        final WebElement customerNav = seleniumHelper.getDriver().findElement(By.partialLinkText("Nachrichten"));
        customerNav.click();

        wait2s.until(ExpectedConditions.urlMatches("/message"));

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/message");
    }

    /**
     * Test if locale change is saved by {@link ch.itds.pbs.portal.util.UserUpdatingSessionLocaleResolver}
     */
    @Test
    public void testLocaleSave() throws InterruptedException {

        seleniumHelper.navigateTo("/?lang=en");
        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Messages")));
        User user = userRepository.findByUsernameWithRoles("3113").get();
        assertEquals(Language.EN, user.getLanguage());

        seleniumHelper.navigateTo("/?lang=de");
        wait2s.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Nachrichten")));
        user = userRepository.findByUsernameWithRoles("3113").get();
        assertEquals(Language.DE, user.getLanguage());

    }
}
