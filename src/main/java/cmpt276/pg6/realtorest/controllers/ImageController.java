package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ImageController {
    @Autowired
    private ImageRepository imageRepo;

    @Autowired
    private PropertyRepository propertyRepo;

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // Dev Page for Images Database
    @GetMapping("/dev/images")
    public String showDevPageImages(Model model, HttpServletRequest request, HttpSession session) {
        // Get all images from the database
        List<Image> images = imageRepo.findAll();
        model.addAttribute("images", images);
        return "dev/images";
    }

    // Adding an image to the database
    @PostMapping("/images/add")
    public String addImage(@RequestParam Map<String, String> newImage, HttpServletRequest request, @RequestParam String redirectUrl, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        int propertyID = Integer.parseInt(newImage.get("propertyID"));
        String imageAddress = newImage.get("imageAddress");
        Image image = new Image(propertyID, imageAddress);
        imageRepo.save(image);
        return "redirect:" + redirectUrl;
    }

    // Fills the database with testing data for each image
    @PostMapping("/images/fill")
    public String fillTestingDataImages(@RequestParam String redirectUrl) {
        List<Property> properties = propertyRepo.findAll();
        for (Property property : properties) {
            if (imageRepo.findByPropertyID(property.getPid()).isEmpty()) {
                imageRepo.save(new Image(property.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/store-image-as-links/Images/Property1/Front.jpg"));
                imageRepo.save(new Image(property.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/store-image-as-links/Images/Property1/Livingroom.jpeg"));
                imageRepo.save(new Image(property.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/store-image-as-links/Images/Property1/Washroom.jpg"));
            }
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes an image from the system.
     */
    @PostMapping("/images/delete/{imageId}")
    public String deleteImage(@PathVariable int imageId, @RequestParam String redirectUrl) {
        imageRepo.deleteById(imageId);
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes all images from the database.
     * This is a dangerous operation and should not be used in a production environment.
     */
    @PostMapping("/images/delete/all")
    public String deleteAllImages(@RequestParam String redirectUrl) {
        imageRepo.deleteAll();
        return "redirect:" + redirectUrl;
    }
}
