package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This controller contains the get mappings for the redirects
 */
@Controller
public class RedirectController {
    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // Since previously users was the only dev page available, we are gonna assume if someone goes to /dev, they want to go to /dev/users
    @GetMapping("/dev")
    public String redirectDev() {
        return "redirect:/dev/users";
    }

    @GetMapping("/dev/user")
    public String redirectDevUser() {
        return "redirect:/dev/users";
    }

    @GetMapping("/dev/property")
    public String redirectDevProperty() {
        return "redirect:/dev/properties";
    }

    // Previously this redirected to /dev, but now we have a dev page for the admins database
    @GetMapping("/dev/admin")
    public String redirectDevAdmin() {
        return "redirect:/dev/admins";
    }

}
