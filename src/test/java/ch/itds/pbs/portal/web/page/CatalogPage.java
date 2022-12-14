package ch.itds.pbs.portal.web.page;


import ch.itds.pbs.portal.web.util.SeleniumHelper;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CatalogPage extends PortalPage {

    public CatalogPage(SeleniumHelper helper) {
        super(helper);
    }

    public static CatalogPage open(SeleniumHelper helper) {
        helper.navigateTo("/catalog");
        return openExisting(helper);
    }

    public static CatalogPage openExisting(SeleniumHelper helper) {
        CatalogPage indexPage = new CatalogPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }


    public List<CatalogTile> getTiles() {
        return driver.findElements(By.cssSelector(".tiles .tile")).stream().map(CatalogTile::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class CatalogTile {


        @FindBy(css = "button[type='submit']")
        protected WebElement addBtn;

        protected WebElement element;

        public CatalogTile(WebElement element) {
            PageFactory.initElements(element, this);
            this.element = element;
        }

    }
}
