package cmpt276.pg6.realtorest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
    @Autowired
    private PropertyRepository propertyRepo;
    @Autowired
    private ImageRepository imageRepo;

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

        Map<Integer, List<Image>> propertyImages = new HashMap<>();
        for (Property property : featuredProperties) {
            List<Image> images = imageRepo.findByPropertyID(property.getPid());
            propertyImages.put(property.getPid(), images);
        }
        model.addAttribute("propertyImages", propertyImages);

        // Display the home page
        return "home";
    }
}
