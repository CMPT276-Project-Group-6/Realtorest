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
public class MainController {
    @Autowired
    private UserRepository userRepo;

    //
    // #region Model Attributes as Global Variables

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // #endregion

    //
    // #region Visitable Pages

    // Home Page
    @GetMapping("/")
    public String showHomePage(Model model, HttpServletRequest request, HttpSession session) {
        // Check if the user is in the session
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        // Display the home page
        return "home";
    }

    // Property Listing Page
    @GetMapping("/property-listing")
    public String showListingPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "propertyListing";
    }

    // Login Page
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/login";
        } else {
            model.addAttribute("user", user);
            // Redirect to the home page if the user is already logged in
            return "redirect:/";
        }
    }

    // Register Page
    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request, HttpSession session) {
        return "users/register";
    }

    // Admin Page
    @GetMapping("/users/all")
    public String showAdminPage(Model model, HttpServletRequest request, HttpSession session) {
        System.out.println("Get all users");
        // Get all users from the database
        List<User> users = userRepo.findAll();
        model.addAttribute("user", users);
        return "users/listAll";
    }

    // #endregion

    //
    // #region Not Visitable Stuff

    // Adding a user to the database, mostly used for testing, a bit obsolete
    // TODO: Combine this with the /register endpoint
    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl, HttpServletResponse response) {
        String name = newUser.get("name");
        String email = newUser.get("email");
        String password = newUser.get("password");
        userRepo.save(new User(name, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    // Login logic
    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<User> userList = userRepo.findByEmailAndPassword(email, password);
        if (userList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            // TODO Add a message to the login page that says "Invalid email or password"
            return "users/login";
        } else {
            // Successful login
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    // Register logic, a bit redundant with the /users/add endpoint, will probably remove it later
    @PostMapping("/register")
    public String register(@RequestParam Map<String, String> newUser, HttpServletResponse response) {
        String name = newUser.get("name");
        String email = newUser.get("email");
        String password = newUser.get("password");
        userRepo.save(new User(name, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:/";
    }

    // Logout by nuking the session
    // TODO: Would it be better to use a POST request for this?
    @GetMapping("/logout")
    public RedirectView destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return new RedirectView("");
    }

    // #endregion
}
