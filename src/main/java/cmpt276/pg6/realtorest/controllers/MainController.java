package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PropertyRepository propertyRepo;

    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void setPropertyRepo(PropertyRepository propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/")
    public String showHomePage(Model model, HttpServletRequest request, HttpSession session) {
        // Check if the user is in the session
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        // Fetch the featured properties from the database
        List<Property> featuredProperties = propertyRepo.findByFeatured(true);
        model.addAttribute("properties", featuredProperties);
        // Display the home page
        return "home";
    }

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

    @GetMapping("/favourites")
    public String showFavourites(HttpServletRequest request, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Set<Property> favouriteProperties = user.getFavouriteProperties();
                model.addAttribute("favouriteProperties", favouriteProperties);
                model.addAttribute("user", user); // Add this line
                return "favourites";
            }
        }
        return "login";
    }
}
