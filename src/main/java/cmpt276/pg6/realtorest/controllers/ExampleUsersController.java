package cmpt276.pg6.realtorest.controllers;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Correct Map import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import cmpt276.pg6.realtorest.models.ExampleUser;
import cmpt276.pg6.realtorest.models.ExampleUserRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ExampleUsersController {
    @Autowired
    private ExampleUserRepository userRepo;

    @GetMapping("/exampleUsers/view")
    public String getAllUsers(Model model) {
        System.out.println("Get all users");
        // Get all users from the database
        List<ExampleUser> users = userRepo.findAll();
        // End of database call.
        model.addAttribute("us", users);
        return "exampleUsers/exampleShowAll";
        // Links to the file in resources/templates/exampleUsers/exampleShowAll.html
    }

    @PostMapping("/exampleUsers/add")
    public String addUser(@RequestParam Map<String, String> newuser, HttpServletResponse response) {
        System.out.println("ADD user");
        String newName = newuser.get("name");
        String newPassword = newuser.get("password");
        int newSize = Integer.parseInt(newuser.get("size"));
        userRepo.save(new ExampleUser(newName, newPassword, newSize));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "exampleUsers/exampleAddedUser";
    }

    @GetMapping("/exampleLogin")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session) {
        ExampleUser user = (ExampleUser) session.getAttribute("session_user");
        if (user == null) {
            return "exampleUsers/exampleLogin";
        } else {
            model.addAttribute("user", user);
            return "exampleUsers/exampleProtected";
        }
    }

    @PostMapping("/exampleLogin")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        // Process the login form
        String name = formData.get("name");
        String password = formData.get("password");
        List<ExampleUser> userList = userRepo.findByNameAndPassword(name, password);
        if (userList.isEmpty()) {
            return "exampleUsers/exampleLogin";
        } else {
            // Successful login
            ExampleUser user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "exampleUsers/exampleProtected";
        }
    }

    @GetMapping("/exampleLogout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "exampleUsers/exampleLogin";
    }
}
