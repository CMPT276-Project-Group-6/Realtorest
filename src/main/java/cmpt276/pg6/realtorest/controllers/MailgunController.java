package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kong.unirest.core.Unirest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;

@Controller
public class MailgunController {
    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/dev/mail")
    public String showDevPageMail(Model model, HttpServletRequest request, HttpSession session) {
        return "dev/mail";
    }

    @PostMapping("/dev/mail/test")
    public String sendTestMail(@RequestParam String redirectUrl) {
        HttpResponse<JsonNode> response = Unirest
            .post("https://api.mailgun.net/v3/REDACTED/messages")
            .basicAuth("api", "REDACTED")
            .field("from", "Mailgun Sandbox <postmaster@REDACTED.mailgun.org>")
            .field("to", "CMPT276 Realtorest <cmpt276projectgroup6@gmail.com>")
            .field("subject", "From Realtorest Application")
            .field("text",
                "This is sent from the Realtorest application. If you received this email, the Mailgun API is working correctly.")
            .asJson();
        return "redirect:" + redirectUrl;
    }
}
