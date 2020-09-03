package ch.itds.pbs.portal.web.page;


import ch.itds.pbs.portal.web.util.SeleniumHelper;
import org.openqa.selenium.support.PageFactory;

public class IndexPage extends PortalPage {

    public IndexPage(SeleniumHelper helper) {
        super(helper);
    }

    public static IndexPage open(SeleniumHelper helper) {
        helper.navigateTo("/");
        IndexPage indexPage = new IndexPage(helper);
        PageFactory.initElements(helper.getDriver(), indexPage);

        return indexPage;
    }
}
