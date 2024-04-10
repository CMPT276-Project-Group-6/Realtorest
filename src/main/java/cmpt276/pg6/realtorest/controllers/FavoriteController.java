package cmpt276.pg6.realtorest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class FavoriteController extends BaseController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PropertyRepository propertyRepo;
    @Autowired
    private ImageRepository imageRepo;

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

    @GetMapping("/favorites")
    public String showFavoritesPage(HttpServletRequest request, HttpSession session, Model model, HttpServletResponse response) {
        Object currentUser = addModelAttributeFromSession(session, model);
        if (!(currentUser instanceof User)) {
            return "redirect:/login";
        }

        User sessionUser = (User) currentUser;
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Property> favoriteProperties = user.getFavoriteProperties();
            model.addAttribute("favoriteProperties", favoriteProperties);

            Map<Integer, List<Image>> propertyImages = new HashMap<>();
            for (Property property : favoriteProperties) {
                List<Image> images = imageRepo.findByPropertyID(property.getPid());
                propertyImages.put(property.getPid(), images);
            }
            model.addAttribute("propertyImages", propertyImages);

             // Pass favorite property IDs to the front end
            Set<Integer> favoritePropertyIds = favoriteProperties.stream().map(Property::getPid).collect(Collectors.toSet());
            model.addAttribute("favoritePropertyIds", favoritePropertyIds);

            return "favorites";
        }

        return "redirect:/login";
    }

    //Add property to favorites
    @PostMapping("/add-favorite/{propertyId}")
    public ResponseEntity<String> addToFavorites(@PathVariable Integer propertyId, HttpServletRequest request, HttpSession session, Model model) {
        Object currentUser = addModelAttributeFromSession(session, model);
        User sessionUser = (User) currentUser;
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            Property property = propertyRepo.findById(propertyId).orElse(null);

            if (user != null && property != null) {
                user.getFavoriteProperties().add(property);
                userRepo.save(user);
                return ResponseEntity.ok("Property added to favorites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User or Property not found");
    }

    //Remove property from favorites
    @DeleteMapping("/remove-favorite/{propertyId}")
    public ResponseEntity<String> removeFromFavorites(@PathVariable Integer propertyId, HttpServletRequest request, HttpSession session, Model model) {
        Object currentUser = addModelAttributeFromSession(session, model);
        User sessionUser = (User) currentUser;
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.getFavoriteProperties().removeIf(property -> property.getPid() == propertyId);
                userRepo.save(user);
                return ResponseEntity.ok("Property removed from favorites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User not found");
    }
}
