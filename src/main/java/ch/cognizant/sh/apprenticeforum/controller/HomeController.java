package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.User;
import ch.cognizant.sh.apprenticeforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    public UserService userService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "home";
    }

    @GetMapping("/home")
    public String showHomePage2(Model model) {
        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "home";
    }

    private User getCurrentlyLoggedInUser() {
        //get current logged in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currently_logged_in_user;

        if(principal instanceof UserDetails) {
            currently_logged_in_user = userService.getByEmail(((UserDetails) principal).getUsername());
        } else {
            currently_logged_in_user = userService.getByEmail(principal.toString());
        }
        return currently_logged_in_user;
    }
}
