package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ImageController {
    @Autowired
    private ImageRepository imageRepo;

    // Dev Page for Images Database
    @GetMapping("/dev/images")
    public String showDevPageImages(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Image> images = imageRepo.findAll();
        model.addAttribute("images", images);
        return "dev/images";
    }

    // Adding a user to the database, used for registering
    @PostMapping("/images/add")
    public String addUser(@RequestParam Map<String, String> newImage, HttpServletRequest request, @RequestParam String redirectUrl, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        int propertyID = Integer.parseInt(newImage.get("propertyID"));
        String imageAddress = newImage.get("imageAddress");
        Image image = new Image(propertyID, imageAddress);
        imageRepo.save(image);
        return "redirect:" + redirectUrl;
    }
}
