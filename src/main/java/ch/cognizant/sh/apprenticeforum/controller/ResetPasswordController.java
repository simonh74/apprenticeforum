package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.User;
import ch.cognizant.sh.apprenticeforum.model.VerificationHelper;
import ch.cognizant.sh.apprenticeforum.service.EmailSenderService;
import ch.cognizant.sh.apprenticeforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Random;

@Controller
public class ResetPasswordController
{
    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password/search-account")
    public String identifyUserViaEmail(Model model) {
        VerificationHelper vh = new VerificationHelper();
        model.addAttribute("verificationHelper", vh);
        return "reset-password-search-account";
    }

    @PostMapping("/reset-password/search-account")
    public String sendEmailToUserWithVerificationCode(@Valid @ModelAttribute VerificationHelper verificationHelper, BindingResult result, Model model) {
        if(result.hasFieldErrors("email")) {
            //in case the email entered in the form doesn't match with the cognizant email format
            return "reset-password-search-account";
        } else if(userService.getByEmail(verificationHelper.getEmail()) == null) {
            //in case the email can't be found in the database
            model.addAttribute("We're sry, but we couldn't find an account under the email you provided. Please check for typos");
            return "reset-password-search-account";
        } else {
            //a user was found in the db with the email provided
            User identified_user = userService.getByEmail(verificationHelper.getEmail());

            //generate a new verification code
            int new_gen_code = gen_verification_code();
            identified_user.setVerification_code(new_gen_code);

            //edit the user in the database and change the verification code
            userService.update(identified_user.getUser_id(), identified_user);

            //send email to the user with the verification code
            try {
                emailSenderService.sendEmail(identified_user, new_gen_code);
            } catch (MailException e) {
                e.printStackTrace();
            }

            return "redirect:/reset-password/verify?id=" + identified_user.getUser_id();
        }
    }

    @GetMapping("/reset-password/verify")
    public String showPageToEnterVerificationCode(@RequestParam(name="id", required = true) int id, Model model) {
        //pass the verificationhelper to the model (html)
        VerificationHelper vh = new VerificationHelper(id);
        model.addAttribute("verificationHelper", vh);
        return "reset-password-verify";
    }

    @PostMapping("/reset-password/verify")
    public String processResetPasswordRequest(@Valid @ModelAttribute VerificationHelper verificationHelper, BindingResult result, Model model) {
        //get the user with the correct verification code from the database
        User user_with_correct_code = userService.getById(verificationHelper.getVh_user_id());

        if (verificationHelper.getVerification_code() != user_with_correct_code.getVerification_code()) {
            model.addAttribute("message", "Sry, the verification code isn't valid for resetting your password.");
            return "reset-password-verify";
        } else {
            return "redirect:/reset-password?id=" + user_with_correct_code.getUser_id();
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam(name="id", required = true) int id, Model model) {
        //pass the verificationhelper to the model (html)
        VerificationHelper vh = new VerificationHelper(id);
        model.addAttribute("verificationHelper", vh);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute VerificationHelper verificationHelper, BindingResult result, Model model) {
        //get the user
        User user_with_forgotten_psw = userService.getById(verificationHelper.getVh_user_id());

        //check for validation errors
        if(result.hasFieldErrors("new_password")) {
            return "reset-password";
        } else {
            if (!verificationHelper.getNew_password().equals(verificationHelper.getConfirm_new_password())) {
                model.addAttribute("message", "The two passwords do not match. Please try again.");
                return "reset-password";
            } else {
                //change password of the user
                user_with_forgotten_psw.setPassword(passwordEncoder.encode(verificationHelper.getNew_password()));
                //update changes in the database
                userService.update(user_with_forgotten_psw.getUser_id(), user_with_forgotten_psw);

                return "redirect:/login";
            }
        }
    }

    private int gen_verification_code() {
        Random rnd = new Random();
        int ver_code = 100000 + rnd.nextInt(900000);
        return ver_code;
    }
}
