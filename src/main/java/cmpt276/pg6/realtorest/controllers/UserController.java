package cmpt276.pg6.realtorest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/login";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/register";
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

    // Adding a user to the database, used for registering
    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, HttpServletRequest request,
        @RequestParam String redirectUrl, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String username = newUser.get("username");
        String email = newUser.get("email");
        String password = newUser.get("password");
        boolean isOnMailingList = newUser.containsKey("isOnMailingList");
        // System.out.println("isOnMailingList: " + isOnMailingList);

        // Check if a user with the same email already exists
        if (!userRepo.findByEmail(email).isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                "An account with this email already exists. Please try logging in.");
            return "redirect:/login";
        }

        User user = new User(username, email, password, isOnMailingList);
        userRepo.save(user);
        request.getSession().setAttribute("session_user", user); // add user to session
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

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
        HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<User> userList = userRepo.findByEmailAndPassword(email, password);
        if (userList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            model.addAttribute("errorMessage", "Invalid Credentials Entered.");
            return "user/login";
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
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }

    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Boolean>> checkLogin(HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        boolean isLoggedIn = sessionUser != null;

        Map<String, Boolean> response = new HashMap<>();
        response.put("loggedIn", isLoggedIn);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/forgotpassword")
   public String showForgotPasswordPage(Model model, HttpServletRequest request, HttpSession session) {
       User user = (User) session.getAttribute("session_user");
       if (user == null) {
           return "users/forgotpassword";
       } else {
           model.addAttribute("user", user);
           return "redirect:/";
       }
   }


    @GetMapping("/resetpassword")
    public String showResetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model, HttpServletRequest request, HttpSession session) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }
    
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            model.addAttribute("token", token);
            return "users/resetpassword";
        } else {
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }


    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam String token, @RequestParam String email, @RequestParam String password, Model model) {
        if (token == null || token.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/login";
        }


        System.out.println("Reset password request received. Email: " + email + ", Token: " + token);
        List<User> users = userRepo.findByEmailAndResetToken(email, token); // Find the user by email and reset token
        if (users.isEmpty()) {
            System.out.println("No user found with provided email and reset token.");
            model.addAttribute("errorMessage", "Invalid reset token.");
            return "users/forgotpassword";
        } else {
            User user = users.get(0);
            user.setPassword(password);
            user.setResetToken(null); // Clear the reset token
            userRepo.save(user);
            return "redirect:/login";
        }
    }

}
