package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.RegisterUser;
import ch.cognizant.sh.apprenticeforum.model.User;
import ch.cognizant.sh.apprenticeforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    public UserService userService;

    @GetMapping("/edit-user")
    public String showEditAccountPage(Model model) {

        //to validate the fields in the edit-account form, we need to repackage the userdetails from the "user" to the "registerUser"
        User loggedin_user = getCurrentlyLoggedInUser();
        RegisterUser editable_user = new RegisterUser();

        editable_user.setRegisterUser_id(loggedin_user.getUser_id());
        editable_user.setEmail(loggedin_user.getEmail());
        editable_user.setFirstname(loggedin_user.getFirstname());
        editable_user.setLastname(loggedin_user.getLastname());
        editable_user.setCognizantid(loggedin_user.getCongizantid());

        //pass the register user to the model to populate the fields
        model.addAttribute("registerUser", editable_user);

        return "edit-account";
    }

    @PostMapping("/edit-user")
    public String processEditUser(@Valid @ModelAttribute RegisterUser registerUser, BindingResult result, Model model) {
        //check for validation errors in the form
        if(result.hasFieldErrors("cognizantid") || result.hasFieldErrors("email") ||
            result.hasFieldErrors("firstname") || result.hasFieldErrors("lastname")) {

            return "edit-account";
        } else {
            //form has no errors
            //repackage the information from the "registerUser" back into the "user"
            User edited_user = getCurrentlyLoggedInUser();

            edited_user.setFirstname(registerUser.getFirstname());
            edited_user.setLastname(registerUser.getLastname());
            edited_user.setEmail(registerUser.getEmail());
            edited_user.setCongizantid(registerUser.getCognizantid());

            //save changes in the database
            userService.update(edited_user.getUser_id(), edited_user);

            return "redirect:/forum";
        }
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
