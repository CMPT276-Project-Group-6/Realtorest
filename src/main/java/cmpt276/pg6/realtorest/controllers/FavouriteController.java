package cmpt276.pg6.realtorest.controllers;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class FavouriteController {
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
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
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

    //Add property to favourites
    @PostMapping("/add-favourite/{propertyId}")
    public ResponseEntity<String> addToFavourites(@PathVariable Integer propertyId, HttpServletRequest request,
        HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            Property property = propertyRepo.findById(propertyId).orElse(null);

            if (user != null && property != null) {
                user.getFavouriteProperties().add(property);
                userRepo.save(user);
                return ResponseEntity.ok("Property added to favourites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User or Property not found");
    }

    //Remove property from favourites
    @DeleteMapping("/remove-favourite/{propertyId}")
    public ResponseEntity<String> removeFromFavourites(@PathVariable Integer propertyId, HttpServletRequest request,
        HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.getFavouriteProperties().removeIf(property -> property.getPid() == propertyId);
                userRepo.save(user);
                return ResponseEntity.ok("Property removed from favourites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User not found");
    }
}
