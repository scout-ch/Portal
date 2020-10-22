package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.web.page.IndexPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

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
}
