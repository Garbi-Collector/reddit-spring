package example.clon.reddit.services;

import example.clon.reddit.entities.NotificationEmail;
import example.clon.reddit.exceptions.SpringRedditException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    @Autowired
    JavaMailSender mailSender;
    @Autowired
    MailContentBuilder mailContentBuilder;


    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springreddit@Email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try{
            mailSender.send(messagePreparator);
            log.info("Activation Email Sent!!");
        }catch (MailException e){
            throw new SpringRedditException("Exception ocurred when sending mail to "+ notificationEmail.getRecipient());
        }
    }
}
