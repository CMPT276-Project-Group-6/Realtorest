package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@Controller
public class MainController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PropertyRepository propertyRepo;

    // #region Model Attributes as Global Variables

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // #endregion



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
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    // Register Page
    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/register";
        } else {
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    // Dev Page for Users Database
    @GetMapping("/dev/users")
    public String showDevPageUsers(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "dev/users";
    }

    // Dev Page for Properties Database
    @GetMapping("/dev/properties")
    public String showDevPageProperties(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "dev/properties";
    }

    // #endregion



    // #region Post mappings

    // Adding a user to the database, used for registering
    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl, HttpServletResponse response) {
        String username = newUser.get("username");
        String email = newUser.get("email");
        String password = newUser.get("password");
        userRepo.save(new User(username, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    /**
     * Fills the users database with testing data.
     */
    @PostMapping("/users/fill")
    public String fillTestingDataUsers(@RequestParam String redirectUrl) {
        userRepo.save(new User("Alice", "alice@email.com", "123"));
        userRepo.save(new User("Bob", "bob@email.com", "456"));
        userRepo.save(new User("Charlie", "charlie@email.com", "789"));
        userRepo.save(new User("David", "david@email.com", "741"));
        userRepo.save(new User("Eve", "eve@email.com", "852"));
        userRepo.save(new User("Frank", "frank@email.com", "963"));
        userRepo.save(new User("Grace", "grace@email.com", "846"));
        userRepo.save(new User("Heidi", "heidi@email.com", "753"));
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes a user from the system.
     */
    @PostMapping("/users/delete/{uid}")
    public String deleteUser(@PathVariable int uid, @RequestParam String redirectUrl) {
        userRepo.deleteById(uid);
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes all users from the database.
     * 
     * This is a dangerous operation and should not be used in a production environment.
     */
    @PostMapping("/users/delete/all")
    public String deleteAllUsers(@RequestParam String redirectUrl) {
        userRepo.deleteAll();
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/properties/add")
    public String addProperty(@RequestParam Map<String, String> newProperty, @RequestParam String redirectUrl, HttpServletResponse response) {
        String name = newProperty.get("name");
        String location = newProperty.get("location");
        int price = Integer.parseInt(newProperty.get("price"));
        int brCount = Integer.parseInt(newProperty.get("brCount"));
        int baCount = Integer.parseInt(newProperty.get("baCount"));
        propertyRepo.save(new Property(name, location, price, brCount, baCount));
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

    // Logout by nuking the session
    // TODO: Would it be better to use a POST request for this?
    @GetMapping("/logout")
    public RedirectView destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return new RedirectView("");
    }



    // #endregion



    // #region Redirects

    @GetMapping("/admin")
    public RedirectView reAdmin() {
        return new RedirectView("/dev");
    }

    @GetMapping("/dev/admin")
    public RedirectView reDevAdmin() {
        return new RedirectView("/dev");
    }

    // Since previously users was the only dev page available, we are gonna assume if someone goes to /dev, they want to go to /dev/users
    @GetMapping("/dev")
    public RedirectView reDev() {
        return new RedirectView("/dev/users");
    }

    // #endregion
}
