package ch.itds.pbs.portal.web.page;


import ch.itds.pbs.portal.web.util.SeleniumHelper;
import org.openqa.selenium.support.PageFactory;

public class MessagePage extends PortalPage {

    public MessagePage(SeleniumHelper helper) {
        super(helper);
    }

    public static MessagePage open(SeleniumHelper helper) {
        helper.navigateTo("/message");
        MessagePage messaagePage = new MessagePage(helper);
        PageFactory.initElements(helper.getDriver(), messaagePage);

        return messaagePage;
    }
}
