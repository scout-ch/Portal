package ch.itds.pbs.portal.web.page.admin;


import ch.itds.pbs.portal.web.page.PortalPage;
import ch.itds.pbs.portal.web.util.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MasterTileIndexPage extends PortalPage {

    @FindBy(css = "a[href$='/masterTile/create']")
    protected WebElement createLink;

    public MasterTileIndexPage(SeleniumHelper helper) {
        super(helper);
    }

    public static MasterTileIndexPage open(SeleniumHelper helper, long midataGroupId) {
        helper.navigateTo("/admin/midataGroup/" + midataGroupId + "/masterTile");
        MasterTileIndexPage indexPage = new MasterTileIndexPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public MasterTileCreatePage clickOnCreate() {
        helper.clickElement(createLink);

        MasterTileCreatePage page = new MasterTileCreatePage(helper);
        PageFactory.initElements(helper.getDriver(), page);

        return page;
    }

    public void clickOnDelete(Long id) {

        WebElement form = helper.getDriver().findElement(By.cssSelector("form[action$='/masterTile/delete/" + id + "']"));
        WebElement btn = form.findElement(By.cssSelector("button"));
        btn.click();
        helper.getDriver().switchTo().alert().accept();

    }
}
