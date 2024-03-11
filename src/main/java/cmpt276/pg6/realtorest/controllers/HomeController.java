package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.*;
import jakarta.servlet.http.*;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @GetMapping("/main")
    public RedirectView redirectMain(Model model, HttpServletRequest request, HttpSession session) {
        return new RedirectView("");
    }

    @GetMapping("/propertylisting")
    public String testPropListing() {
        return "propertylisting";
    }
}
