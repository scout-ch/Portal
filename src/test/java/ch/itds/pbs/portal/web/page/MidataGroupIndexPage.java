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
public class MidataGroupIndexPage extends PortalPage {

    public MidataGroupIndexPage(SeleniumHelper helper) {
        super(helper);
    }

    public static MidataGroupIndexPage open(SeleniumHelper helper) {
        helper.navigateTo("/midataGroup");
        MidataGroupIndexPage indexPage = new MidataGroupIndexPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public List<MidataGroupIndexPage.MidataGroupRow> getTiles() {
        return driver.findElements(By.cssSelector("table tr[id^=group]")).stream().map(MidataGroupIndexPage.MidataGroupRow::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class MidataGroupRow {


        @FindBy(css = "a[href$=defaultTile]")
        protected WebElement defaultTileBtn;

        @FindBy(css = "a[href$=masterTile]")
        protected WebElement masterTileBtn;

        protected WebElement element;

        public MidataGroupRow(WebElement element) {
            PageFactory.initElements(element, this);
            this.element = element;
        }

    }
}
