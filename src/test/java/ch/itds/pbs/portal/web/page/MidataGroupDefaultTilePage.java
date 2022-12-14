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
public class MidataGroupDefaultTilePage extends PortalPage {

    @FindBy(partialLinkText = "Kachel aus Katalog hinzufügen")
    protected WebElement addTileBtn;

    public MidataGroupDefaultTilePage(SeleniumHelper helper) {
        super(helper);
    }

    public static MidataGroupDefaultTilePage open(SeleniumHelper helper, Long groupId) {
        helper.navigateTo("/midataGroup/" + groupId + "/defaultTile");
        return openExisting(helper);
    }

    public static MidataGroupDefaultTilePage openExisting(SeleniumHelper helper) {
        MidataGroupDefaultTilePage indexPage = new MidataGroupDefaultTilePage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public List<GroupDefaultTileRow> getTiles() {
        return driver.findElements(By.cssSelector("table tr[data-id]")).stream().map(GroupDefaultTileRow::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class GroupDefaultTileRow {


        @FindBy(css = ".grip-handle")
        protected WebElement gripHandle;

        @FindBy(css = "button")
        protected WebElement deleteBtn;

        protected WebElement element;

        public GroupDefaultTileRow(WebElement element) {
            PageFactory.initElements(element, this);
            this.element = element;
        }

    }
}
