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
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepo;

      // Login Page for admin
    @GetMapping("/adminlogin")
    public String showAdminLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "users/adminlogin";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("admin", admin);
            return "redirect:/";
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
            model.addAttribute("user", admin);
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public RedirectView destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return new RedirectView("");
    }

}
