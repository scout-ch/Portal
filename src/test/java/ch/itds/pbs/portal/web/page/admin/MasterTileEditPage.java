package ch.itds.pbs.portal.web.page.admin;


import ch.itds.pbs.portal.web.page.PortalPage;
import ch.itds.pbs.portal.web.util.SeleniumHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class MasterTileEditPage extends PortalPage {

    @FindBy(css = "button[type='submit']")
    protected WebElement submit;

    @FindBy(name = "title.de")
    protected WebElement titleDe;

    @FindBy(name = "content.de")
    protected WebElement contentDe;

    @FindBy(name = "url.de")
    protected WebElement urlDe;

    @FindBy(name = "category")
    protected WebElement category;

    @FindBy(name = "imageUpload")
    protected WebElement imageUpload;

    @FindBy(name = "backgroundColor")
    protected WebElement backgroundColor;

    @FindBy(name = "apiKey")
    protected WebElement apiKey;

    @FindBy(id = "btn-reload-api")
    protected WebElement apiKeyRefreshBtn;

    @FindBy(name = "enabled")
    protected WebElement enabled;

    public MasterTileEditPage(SeleniumHelper helper) {
        super(helper);
    }

    public static MasterTileEditPage open(SeleniumHelper helper, long id, long midataGroupId) {
        helper.navigateTo("/midataGroup/" + midataGroupId + "/masterTile/edit/" + id);
        MasterTileEditPage indexPage = new MasterTileEditPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }

    public void requestApiKey() {
        apiKeyRefreshBtn.click();
    }

    public void fillForm(String title, String content, String url, String categoryValue, String backgroundColorValue, String fileName) {

        if (title != null) {
            titleDe.clear();
            titleDe.sendKeys(title);
        }

        if (content != null) {
            contentDe.clear();
            contentDe.sendKeys(content);
        }

        if (url != null) {
            urlDe.clear();
            urlDe.sendKeys(url);
        }

        if (categoryValue != null) {
            Select categorySelect = new Select(category);
            categorySelect.selectByValue(categoryValue);
        }

        if (backgroundColorValue != null) {
            Select backgroundColorSelect = new Select(backgroundColor);
            backgroundColorSelect.selectByValue(backgroundColorValue);
        }

        if (fileName != null) {
            if (driver instanceof RemoteWebDriver remoteWebDriver) {
                remoteWebDriver.setFileDetector(new LocalFileDetector());
            }
            imageUpload.clear();
            imageUpload.sendKeys(fileName);
        }
    }

    public void submit() {
        submit.click();
    }

}
