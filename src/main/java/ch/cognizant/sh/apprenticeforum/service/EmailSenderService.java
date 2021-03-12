package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService
{
    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService (JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendNotification(User user, int code) throws MailException {
        //send Email
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setFrom("simhus78@gmail.com");
        mail.setSubject("Verify by email with a verification code");
        mail.setText("code is: " + code);

        javaMailSender.send(mail);
    }
}
