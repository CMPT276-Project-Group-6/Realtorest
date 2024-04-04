package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This controller contains the get mappings for the redirects
 */
@Controller
public class RedirectController {

    // Since previously users was the only dev page available, we are gonna assume if someone goes to /dev, they want to go to /dev/users
    @GetMapping("/dev")
    public RedirectView redirectDev() {
        return new RedirectView("/dev/users");
    }

    @GetMapping("/dev/user")
    public RedirectView redirectDevUser() {
        return new RedirectView("/dev/users");
    }

    @GetMapping("/dev/property")
    public RedirectView redirectDevProperty() {
        return new RedirectView("/dev/properties");
    }

    // Previously this redirected to /dev, but now we have a dev page for the admins database
    @GetMapping("/dev/admin")
    public RedirectView redirectDevAdmin() {
        return new RedirectView("/dev/admins");
    }

}
