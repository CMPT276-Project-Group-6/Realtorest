package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
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

    @Autowired
    private AdminRepository adminRepo;

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

    // Admin Page
    @GetMapping("/dev/admin")
    public String showAdminPage(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<User> users = userRepo.findAll();
        model.addAttribute("user", users);
        return "dev/admin";
    }

    // #endregion

    //
    // #region Not Visitable Stuff

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

    // 
    // #region Redirects

    @GetMapping("/admin")
    public RedirectView reAdmin() {
        return new RedirectView("/dev/admin");
    }

    // #endregion

     @PostMapping("/users/addAdmin")
    public String addAdmin(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl, HttpServletResponse response) {
        String adminName = newUser.get("username");
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
	public ModelAndView getAllUsers() {
		ModelAndView mav = new ModelAndView("list-users");
		mav.addObject("users", userRepo.findAll());
		return mav;
	}//lists all users in database
}


