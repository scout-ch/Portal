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
public class IndexPage extends PortalPage {

    @FindBy(id = "toggleTileEditor")
    protected WebElement toggleTileEditorBtn;

    @FindBy(partialLinkText = "Kachel hinzufügen")
    protected WebElement addTileBTn;

    public IndexPage(SeleniumHelper helper) {
        super(helper);
    }

    public static IndexPage open(SeleniumHelper helper) {
        helper.navigateTo("/");
        IndexPage indexPage = new IndexPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public List<IndexTile> getTiles() {
        return driver.findElements(By.cssSelector(".tiles .tile")).stream().map(IndexTile::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class IndexTile {


        @FindBy(css = ".btn-move")
        protected WebElement moveBtn;

        @FindBy(css = ".btn-delete")
        protected WebElement deleteBtn;

        protected WebElement element;

        public IndexTile(WebElement element) {
            PageFactory.initElements(element, this);
            this.element = element;
        }

    }
}
