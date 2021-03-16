package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.*;
import ch.cognizant.sh.apprenticeforum.service.EmailSenderService;
import ch.cognizant.sh.apprenticeforum.service.RoleService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Controller
public class RegisterController
{
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("registerUser", new RegisterUser());
        return "register";
    }

    @PostMapping("/register")
    public String postRequestRegisterUser(@Valid @ModelAttribute RegisterUser registerUser, BindingResult result, Model model)
    {
        if(result.hasErrors()) {
            model.addAttribute("registerUser", registerUser);
            return "register";
        }
        else {
            if(!registerUser.getPassword().equals(registerUser.getConfirmpassword())) {
                model.addAttribute("message", "The two passwords do not match");
                return "register";
            } else if(userService.getByEmail(registerUser.getEmail()) != null) {
                model.addAttribute("message", "A user already exists with the email you entered");
                return "register";
            }
            else {
                String encodedpsw = passwordEncoder.encode(registerUser.getPassword());
                int ver_code = gen_verification_code();

                Set<Role> register_urs_roles = new HashSet<Role>();
                register_urs_roles.add(roleService.getById(1));

                Set<Post> upvoted_posts = new HashSet<Post>();
                Set<Post> downvoted_posts = new HashSet<Post>();

                User new_user = new User(0,
                        registerUser.getFirstname(),
                        registerUser.getLastname(),
                        registerUser.getEmail(),
                        registerUser.getCognizantid(),
                        encodedpsw,
                        false,
                        ver_code,
                        register_urs_roles
                );
                userService.add(new_user);

                //send email
                try {
                    emailSenderService.sendEmail(new_user, ver_code);
                } catch (MailException e) {
                    e.printStackTrace();
                }

                model.addAttribute("message", "Congratulations! You've been successfully registered.");
                User to_be_ver_usr = userService.getByEmail(new_user.getEmail());
                return "redirect:/verify?id=" + to_be_ver_usr.getUser_id();
            }
        }
    }

    @GetMapping("/verify")
    public String verifyEmailAddress(@RequestParam(name="id", required = true) int id, Model model) {
        User unverified_user = userService.getById(id);
        VerificationHelper vh = new VerificationHelper(unverified_user.getUser_id());
        vh.setEmail("");
        vh.setNew_password("");
        vh.setConfirm_new_password("");
        vh.setVerification_code(0);
        model.addAttribute("verificationHelper", vh);
        return "verifyregistration";
    }

    @PostMapping("/verify")
    public String processVerificationCode(@Valid @ModelAttribute VerificationHelper verificationHelper, BindingResult result, Model model) {
        User usr_with_code = userService.getById(verificationHelper.getVh_user_id());
        if(result.hasErrors()) {
            if(result.hasFieldErrors("email")) {
                if (verificationHelper.getVerification_code() != usr_with_code.getVerification_code()) {
                    model.addAttribute("message", "Sry, the verification code isn't valid");
                    return "verifyregistration";
                }
                else {
                    usr_with_code.setVerified(true);
                    userService.update(verificationHelper.getVh_user_id(), usr_with_code);
                }
            } else if(result.hasFieldErrors("new_password")){
                if (verificationHelper.getVerification_code() != usr_with_code.getVerification_code()) {
                    model.addAttribute("message", "Sry, the verification code isn't valid");
                    return "verifyregistration";
                }
                else {
                    usr_with_code.setVerified(true);
                    userService.update(verificationHelper.getVh_user_id(), usr_with_code);
                }
            } else {
                model.addAttribute("verificationHelper", verificationHelper);
                return "verifyregistration";
            }

        }
        else {
            if (verificationHelper.getVerification_code() != usr_with_code.getVerification_code()) {
                model.addAttribute("message", "Sry, the verification code isn't valid");
                return "verifyregistration";
            }
            else {
                usr_with_code.setVerified(true);
                userService.update(verificationHelper.getVh_user_id(), usr_with_code);
            }
        }
        model.addAttribute("sucessmsg", "you're successfully verified! Now you can login");
        return "redirect:/login";
    }

    private int gen_verification_code() {
        Random rnd = new Random();
        int ver_code = 100000 + rnd.nextInt(900000);
        return ver_code;
    }
}
