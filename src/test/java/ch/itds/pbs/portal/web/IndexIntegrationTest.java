package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.web.page.IndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexIntegrationTest extends IntegrationTest {
    @Test
    public void testIndex() throws InterruptedException {


        IndexPage dashboardPage = IndexPage.open(seleniumHelper);

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

        final WebElement customerNav = seleniumHelper.getDriver().findElement(By.partialLinkText("Nachrichten"));
        customerNav.click();

        Thread.sleep(1500);

        final String currentUrl = seleniumHelper.getDriver().getCurrentUrl();
        assertThat(currentUrl).endsWith("/message");
    }

    /**
     * Test if locale change is saved by {@link ch.itds.pbs.portal.util.UserUpdatingSessionLocaleResolver}
     */
    @Test
    public void testLocaleSave() throws InterruptedException {

        seleniumHelper.navigateTo("/?lang=en");
        Thread.sleep(1500);
        User user = userRepository.findByUsernameWithRoles("3113").get();
        assertEquals(Language.EN, user.getLanguage());

        seleniumHelper.navigateTo("/?lang=de");
        Thread.sleep(1500);
        user = userRepository.findByUsernameWithRoles("3113").get();
        assertEquals(Language.DE, user.getLanguage());

    }
}
