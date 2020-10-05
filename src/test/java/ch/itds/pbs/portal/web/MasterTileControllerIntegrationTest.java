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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        List<MasterTile> masterTiles = masterTileRepository.findAll();
        MasterTile masterTile;
        if (masterTiles.isEmpty()) {
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
