package ch.cognizant.sh.apprenticeforum.controller;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginLogoutController
{
    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(Model model) {
        return "logout";
    }

    @GetMapping("/login-error")
    public String errorLogin(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if(session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if(ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errormsg", "Either your email or password is invalid");
        return "login";
    }
}
