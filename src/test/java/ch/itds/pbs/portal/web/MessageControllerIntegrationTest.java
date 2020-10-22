package ch.itds.pbs.portal.web;

import ch.itds.pbs.portal.repo.MessageRepository;
import ch.itds.pbs.portal.web.page.MessagePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageControllerIntegrationTest extends IntegrationTest {

    @Autowired
    MessageRepository messageRepository;

    @Test
    public void testIndex() throws InterruptedException {


        MessagePage page = MessagePage.open(seleniumHelper);

        Thread.sleep(1500);


        System.out.println("current url: " + seleniumHelper.getDriver().getCurrentUrl());

    }


}
