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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
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
    private AdminRepository adminRepo;

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
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
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

    @GetMapping("/dev/admins")
    public String showDevPageAdmins(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Admin> admins = adminRepo.findAll();
        model.addAttribute("admins", admins);
        return "dev/admins";
    }

    // #endregion

    // #region Post mappings

    // #region Users

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
     * This is a dangerous operation and should not be used in a production environment.
     */
    @PostMapping("/users/delete/all")
    public String deleteAllUsers(@RequestParam String redirectUrl) {
        userRepo.deleteAll();
        return "redirect:" + redirectUrl;
    }

    // #endregion

    // #region Properties

    @PostMapping("/properties/add")
    public String addProperty(@RequestParam Map<String, String> newProperty, @RequestParam String redirectUrl, HttpServletResponse response) {
        String name = newProperty.get("name");
        String street = newProperty.get("street");
        String city = newProperty.get("city");
        String province = newProperty.get("province");
        String zipCode = newProperty.get("zipCode");
        String description = newProperty.get("description");
        int price = Integer.parseInt(newProperty.get("price"));
        double area = Double.parseDouble(newProperty.get("area"));
        int brCount = Integer.parseInt(newProperty.get("brCount"));
        int baCount = Integer.parseInt(newProperty.get("baCount"));
        propertyRepo.save(new Property(name, street, city, province, zipCode, description, price, area, brCount, baCount));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    /**
     * Fills the properties database with testing data.
     */
    @PostMapping("/properties/fill")
    public String fillTestingDataProperties(@RequestParam String redirectUrl) {
        propertyRepo.save(new Property("Alice's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Bob's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Charlie's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("David's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Eve's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Frank's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Grace's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        propertyRepo.save(new Property("Heidi's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6", "Nothing much...", 1000000, 1500.50, 3, 2));
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes a property from the system.
     */
    @PostMapping("/properties/delete/{pid}")
    public String deleteProperty(@PathVariable int pid, @RequestParam String redirectUrl) {
        propertyRepo.deleteById(pid);
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes all properties from the database.
     * This is a dangerous operation and should not be used in a production environment.
     */
    @PostMapping("/properties/delete/all")
    public String deleteAllProperties(@RequestParam String redirectUrl) {
        propertyRepo.deleteAll();
        return "redirect:" + redirectUrl;
    }

    // #endregion

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
            model.addAttribute("error", "Invalid Credentials Entered.");
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
        return new RedirectView("/");
    }

    // #endregion

    @PostMapping("/admins/add")
    public String addAdmin(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl, HttpServletResponse response) {
        String adminName = newUser.get("adminname");
        String email = newUser.get("email");
        String password = newUser.get("password");
        adminRepo.save(new Admin(adminName, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/registerAdmin")
    public String showRegisterAdminPage(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "users/registerAdmin";
        } else {
            model.addAttribute("admin", admin);
            return "redirect:/";
        }
    }

    // Login Page for admin
    @GetMapping("/adminlogin")
    public String showAdminLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "users/adminlogin";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("admin", admin);
            return "protected";
        }
    }

    @PostMapping("/adminlogin")
    public String adminlogin(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<Admin> adminList = adminRepo.findByEmailAndPassword(email, password);
        if (adminList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            // TODO Add a message to the login page that says "Invalid email or password"
            return "users/adminlogin";
        } else {
            // Successful login
            Admin admin = adminList.get(0);
            request.getSession().setAttribute("session_user", admin);
            model.addAttribute("admin", admin);
            return "protected";
        }
    }

    @GetMapping({"/listUsers"})
    public ModelAndView getAllUsers(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            throw new SecurityException("This is a protected page");
        } else {
            ModelAndView mav = new ModelAndView("list-users");
            mav.addObject("users", userRepo.findAll());
            return mav;
        }
    }// lists all users in database for admin

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
