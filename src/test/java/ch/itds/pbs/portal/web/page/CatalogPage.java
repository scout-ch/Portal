package ch.itds.pbs.portal.web.page;


import ch.itds.pbs.portal.web.util.SeleniumHelper;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

@Getter
@Setter
public class CatalogPage extends PortalPage {

    @FindBy(css = ".tiles .tile")
    protected List<CatalogTile> tiles;

    public CatalogPage(SeleniumHelper helper) {
        super(helper);
    }

    public static CatalogPage open(SeleniumHelper helper) {
        helper.navigateTo("/");
        CatalogPage indexPage = new CatalogPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public static class CatalogTile {


        @FindBy(css = ".btn-move")
        protected WebElement createBtn;

        public CatalogTile(WebElement element) {
            PageFactory.initElements(element, this);
        }

    }
}
