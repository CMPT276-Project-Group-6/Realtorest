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

import cmpt276.pg6.realtorest.models.Admin;
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
    public String showLoginPage(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        // Set no-cache headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/login";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        // Set no-cache headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
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

    // Admin Page for Users Database
    @GetMapping("/admin/users")
    public String showAdminPageUsers(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        // Get all users from the database
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("admin", admin);
        return "admin/users";
    }

    // Adding a user to the database, used for registering
    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, HttpServletRequest request, @RequestParam String redirectUrl, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String email = newUser.get("email");
        String password = newUser.get("password");
        String username = newUser.get("username");
        String firstName = newUser.get("firstName");
        String lastName = newUser.get("lastName");
        boolean isOnMailingList = newUser.containsKey("isOnMailingList");
        // System.out.println("isOnMailingList: " + isOnMailingList);
        // check if the username is unique
        if (userRepo.findByUsername(username).size() > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Username is already taken. Please pick another one.");
            return "redirect:/register";
        }

        // Check if a user with the same email already exists
        if (!userRepo.findByEmail(email).isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                "An account with this email already exists. Please try logging in.");
            return "redirect:/login";
        }

        User user = new User(email, password, username, firstName, lastName, isOnMailingList);
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
        userRepo.save(new User("alice@email.com", "123", "alice123", "Alice", "Smith", false));
        userRepo.save(new User("bob@email.com", "456", "bob456", "Bob", "Johnson", false));
        userRepo.save(new User("charlie@email.com", "789", "charlie789", "Charlie", "Williams", false));
        userRepo.save(new User("david@email.com", "741", "david741", "David", "Brown", false));
        userRepo.save(new User("eve@email.com", "852", "eve852", "Eve", "Jones", false));
        userRepo.save(new User("cmpt276projectgroup6+test1@gmail.com", "123", "mailgunTester1", "First", "Tester", true));
        userRepo.save(new User("cmpt276projectgroup6+test2@gmail.com", "123", "mailgunTester2", "Second", "Tester", true));
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
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
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
    public String destroySession(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        //no-cache headers to ensure the final page isn't cached by the browser
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

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

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/forgot-password";
        } else {
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam(value = "token", required = false) String token, @RequestParam(value = "email", required = false) String email,
        Model model, HttpServletRequest request, HttpSession session) {
        if (token == null || token.isEmpty() || email == null || email.isEmpty()) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            model.addAttribute("token", token);
            model.addAttribute("email", email);
            return "user/reset-password";
        } else {
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String email, @RequestParam String password, Model model) {
        if (token == null || token.isEmpty() || email == null || email.isEmpty() || password == null
            || password.isEmpty()) {
            return "redirect:/login";
        }

        System.out.println("Reset password request received. Email: " + email + ", Token: " + token);
        List<User> users = userRepo.findByEmailAndResetToken(email, token); // Find the user by email and reset token
        if (users.isEmpty()) {
            System.out.println("No user found with provided email and reset token.");
            model.addAttribute("errorMessage", "Invalid reset token.");
            return "user/forgot-password";
        } else {
            User user = users.get(0);
            user.setPassword(password);
            user.setResetToken(null); // Clear the reset token
            userRepo.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/settings")
    public String goSettings(Model model, HttpServletRequest request, HttpSession session) {
        // Check if the user is in the session
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "user/settings";
    }

    @PostMapping("/settings")
    public String changeInformation(@RequestParam Map<String, String> formData, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if the user is in the session
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            // Check if username is unique
            List<User> existingUsersByUsername = userRepo.findByUsername(formData.get("username"));
            if (!existingUsersByUsername.isEmpty()) {
                // Check if the found user is not the current user being edited
                boolean foundCurrent = false;
                for (User existingUser : existingUsersByUsername) {
                    if (existingUser.getUid() == user.getUid()) {
                        foundCurrent = true;
                        break;
                    }
                }
                if (!foundCurrent) {
                    // Username is not unique
                    redirectAttributes.addFlashAttribute("usernameError", "Username is already taken");
                    return "redirect:/settings";
                }
            }

            // Check if email is unique
            List<User> existingUsersByEmail = userRepo.findByEmail(formData.get("email"));
            if (!existingUsersByEmail.isEmpty()) {
                // Check if the found user is not the current user being edited
                boolean foundCurrent = false;
                for (User existingUser : existingUsersByEmail) {
                    if (existingUser.getUid() == user.getUid()) {
                        foundCurrent = true;
                        break;
                    }
                }
                if (!foundCurrent) {
                    // Email is not unique
                    redirectAttributes.addFlashAttribute("emailError", "Email is already registered");
                    return "redirect:/settings";
                }
            }

            // Update user information if no errors are found
            user.setEmail(formData.get("email"));
            user.setPassword(formData.get("password"));
            user.setUsername(formData.get("username"));
            user.setFirstName(formData.get("firstName"));
            user.setLastName(formData.get("lastName"));
            user.setOnMailingList(formData.containsKey("isOnMailingList"));
            userRepo.save(user);

            // Add the user object to the model
            model.addAttribute("user", user);
        }
        // Redirect to the settings page without any errors
        return "redirect:/settings";
    }
}
