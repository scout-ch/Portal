package ch.itds.pbs.portal.web.page;

import ch.itds.pbs.portal.web.util.SeleniumHelper;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

@Getter
@Setter
public class PortalPage {

    protected SeleniumHelper helper;
    protected WebDriver driver;

    @FindBy(css = "nav.sidebar")
    protected WebElement navSidebar;

    @FindBy(css = "a[href='/']")
    protected WebElement dashboardLink;

    @FindBy(css = ".privacy-btn.btn")
    protected WebElement privacyBtn;

    public PortalPage(WebDriver driver) {
        this.driver = driver; // required for PageFactory.initElements...
    }

    public PortalPage(SeleniumHelper helper) {
        this.helper = helper;
        PageFactory.initElements(helper.getDriver(), PortalPage.class);
    }

    public IndexPage clickOnDashboard() {
        helper.clickElement(dashboardLink);

        IndexPage indexPage = new IndexPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public void acceptPrivacyNotice() {
        try {
            if (privacyBtn != null && privacyBtn.isDisplayed()) {
                privacyBtn.click();
            }
        } catch (NoSuchElementException e) {
            // noop - all ok, button already away
        }
    }

}
