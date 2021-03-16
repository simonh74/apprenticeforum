package ch.cognizant.sh.apprenticeforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String showHomePage(Model model) {
        return "home";
    }

    @GetMapping("/home")
    public String showHomePage2(Model model) {
        return "home";
    }
}
