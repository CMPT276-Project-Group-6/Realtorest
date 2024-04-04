package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AdminRepository adminRepo;

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // Dev Page for Admins Database
    @GetMapping("/dev/admins")
    public String showDevPageAdmins(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Admin> admins = adminRepo.findAll();
        model.addAttribute("admins", admins);
        return "dev/admins";
    }

    @PostMapping("/admins/add")
    public String addAdmin(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl,
        HttpServletResponse response) {
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
    public String adminlogin(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
        HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<Admin> adminList = adminRepo.findByEmailAndPassword(email, password);
        if (adminList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            model.addAttribute("errorMessage", "Invalid Credentials Entered.");
            return "users/adminlogin";
        } else {
            // Successful login
            Admin admin = adminList.get(0);
            request.getSession().setAttribute("session_user", admin);
            model.addAttribute("admin", admin);
            return "protected";
        }
    }

    // lists all users in database for admin
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
    }
}
