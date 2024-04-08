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
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController extends BaseController {
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

    // Home Page for Admin, should not be accessible without logging in as an admin
    @GetMapping("/admin")
    public String showAdminHomepage(Model model, HttpServletRequest request, HttpSession session) {
        addModelAttributeFromSession(session, model);
        return "admin/home";
    }

    // Login Page for admin
    @GetMapping("/admin/login")
    public String showAdminLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        Object currentUser = addModelAttributeFromSession(session, model);
        if (currentUser instanceof Admin) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    // Dev Page for Admins Database
    @GetMapping("/dev/admins")
    public String showDevPageAdmins(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Admin> admins = adminRepo.findAll();
        model.addAttribute("admins", admins);
        return "dev/admins";
    }

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
        HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<Admin> adminList = adminRepo.findByEmailAndPassword(email, password);
        if (adminList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            model.addAttribute("errorMessage", "Invalid Credentials Entered.");
            return "admin/login";
        } else {
            // Successful login
            Admin admin = adminList.get(0);
            request.getSession().setAttribute("session_user", admin);
            model.addAttribute("admin", admin);
            return "redirect:/admin";
        }
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
}
