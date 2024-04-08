package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
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
}
