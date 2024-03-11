package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    /**
     * A way to grab the current URL, using this to refresh the webpage after doing something.
     * 
     * Kevin: Yeah I know this is a weird hack but please don't touch this for now.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/users/all")
    public String getAllUsers(Model model) {
        System.out.println("Get all users");
        // Get all users from the database
        List<User> users = userRepo.findAll();
        // End of database call.
        model.addAttribute("user", users);
        return "users/listAll";
        // Links to the file in resources/templates/exampleUsers/exampleShowAll.html
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/login";
        } else {
            model.addAttribute("user", user);
            return "users/protected";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<User> userList = userRepo.findByEmailAndPassword(email, password);
        if (userList.isEmpty()) {
            // if there are no user accounts in db
            return "users/login";
        } else {
            // Successful login
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "users/protected";
        }
    }

    @GetMapping("/logout")
    public RedirectView destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return new RedirectView("");
    }

    /**
     * Adds a new user to the system.
     */
    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl, HttpServletResponse response) {
        String name = newUser.get("name");
        String email = newUser.get("email");
        String password = newUser.get("password");
        userRepo.save(new User(name, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model, HttpServletRequest request, HttpSession session) {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam Map<String, String> newUser, HttpServletResponse response) {
        String name = newUser.get("name");
        String email = newUser.get("email");
        String password = newUser.get("password");
        userRepo.save(new User(name, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "home";
    }
}
