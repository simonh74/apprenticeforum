package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EmailSenderService
{
    @Autowired
    public UserService userService;


    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService (JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationCode(User user, int code) throws MailException {
        //instantiate simple Mail Message
        SimpleMailMessage mail = new SimpleMailMessage();

        //add email details
        mail.setTo(user.getEmail());
        mail.setFrom("simhus78@gmail.com");
        mail.setSubject("Verify by email with a verification code");
        mail.setText("The verification code is: " + code + ". Please enter this code into the field to verify yourself.");

        //send email
        javaMailSender.send(mail);
    }

    public void sendNewQuestionNotificationToEveryone(User author, String title) {
        //instantiate simple Mail Message
        SimpleMailMessage mail = new SimpleMailMessage();


        //get list of all users' emails in a string array
        ArrayList<String> recipients_arr_list = new ArrayList<>();
        for(User useritr : userService.getAll()) {
            recipients_arr_list.add(useritr.getEmail());
        }

        //convert the arraylist into a string array
        String[] recipients = new String[recipients_arr_list.size()];
        recipients = recipients_arr_list.toArray(recipients);

        //add email details
        mail.setTo(recipients);
        mail.setFrom("simhus78@gmail.com");
        mail.setSubject("A new Question has just been posted");
        mail.setText(author.getFirstname() + " " + author.getLastname() + " has just posted a new question with the title: " + title);

        javaMailSender.send(mail);
    }
}
