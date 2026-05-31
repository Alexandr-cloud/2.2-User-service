package com.aston.listener;

import com.aston.service.EmailService;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserEventListenerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(
            new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP)
    ).withPerMethodLifecycle(true);

    @Autowired
    private EmailService emailService;

    @Test
    void testAccountCreatedEmailSent() throws Exception {
        emailService.sendAccountCreatedEmail("test@example.com");

        Thread.sleep(1000);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getSubject()).isEqualTo("Account Created");
    }

    @Test
    void testAccountDeletedEmailSent() throws Exception {
        emailService.sendAccountDeletedEmail("test@example.com");

        Thread.sleep(1000);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getSubject()).isEqualTo("Account Deleted");
    }
}