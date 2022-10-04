package ch.itds.pbs.portal.web.page;


import ch.itds.pbs.portal.web.util.SeleniumHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class MessagePage extends PortalPage {

    @FindBy(css = "ul.messages")
    protected WebElement messageList;

    @FindBy(css = "ul.messages li.message")
    protected List<WebElement> messageItems;

    public MessagePage(SeleniumHelper helper) {
        super(helper);
    }

    public static MessagePage open(SeleniumHelper helper) {
        helper.navigateTo("/message");
        MessagePage messagePage = new MessagePage(helper);
        PageFactory.initElements(helper.getDriver(), messagePage);

        return messagePage;
    }

    public int getMessageSize() {
        return messageItems.size();
    }

    public void openMessage(int index) throws InterruptedException {
        WebElement msg = messageItems.get(index);

        WebElement openBtn = msg.findElement(By.cssSelector(".controls .indicator-open"));
        openBtn.click();
        Thread.sleep(500);
    }

    public MessagePage deleteMessage(int index) throws InterruptedException {
        openMessage(index);

        WebElement msg = messageItems.get(index);

        WebElement deleteBtn = msg.findElement(By.cssSelector(".controls button.btn-delete"));
        deleteBtn.click();

        Thread.sleep(200);

        Alert alert = helper.getDriver().switchTo().alert();
        alert.accept();

        Thread.sleep(300);

        MessagePage messagePage = new MessagePage(helper);
        PageFactory.initElements(helper.getDriver(), messagePage);
        return messagePage;
    }

}
