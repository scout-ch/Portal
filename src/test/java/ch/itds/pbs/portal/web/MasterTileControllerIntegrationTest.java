package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.web.page.admin.MasterTileCreatePage;
import ch.itds.pbs.portal.web.page.admin.MasterTileEditPage;
import ch.itds.pbs.portal.web.page.admin.MasterTileIndexPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class MasterTileControllerIntegrationTest extends IntegrationTest {

    @Autowired
    MasterTileRepository masterTileRepository;

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

        MasterTile masterTile = masterTileRepository.findAll().get(0);

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
    }
}
