package cmpt276.pg6.realtorest.controllers;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Correct Map import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

   /* @GetMapping("/exampleUsers/view")
    public String getAllUsers(Model model) {
        System.out.println("Get all users");
        // Get all users from the database
        List<ExampleUser> users = userRepo.findAll();
        // End of database call.
        model.addAttribute("us", users);
        return "exampleUsers/exampleShowAll";
        // Links to the file in resources/templates/exampleUsers/exampleShowAll.html
    }
*/

    
/* ACCOUNT CREATION NOT WORKING YET
    @PostMapping("/Users/CreateAccount")
    public String addUser(@RequestParam Map<String, String> newuser, HttpServletResponse response) {
        System.out.println("ADD user");
        String newName = newuser.get("name");
        String newEmail = newuser.get("email");
        String newPassword = newuser.get("password");
        userRepo.save(new User(newName, newEmail, newPassword));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "Users/accountCreated";
    }//gets account details from form and stores in db
*/
    @GetMapping("/Login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            //System.out.println("Bad login");
            model.addAttribute("message", "Invalid credentials entered");
            return "Users/Login";
        } //no such user exists in db
        else {
            model.addAttribute("user", user);
            return "Users/Protected";
        }
    }//process login to see if user exists in system

    @PostMapping("/Login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<User> userList = userRepo.findByEmailAndPassword(email, password);
        if (userList.isEmpty()) {
            return "Users/Login";
        } //if there are no user accounts in db
        else 
        {
            // Successful login
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "Users/Protected";
        }
    }

    @GetMapping("/Logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "Users/Login";
    }
}
