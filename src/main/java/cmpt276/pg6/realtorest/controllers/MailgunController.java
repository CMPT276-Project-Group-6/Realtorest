package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

@Controller
public class MailgunController {
    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private static final String MAILGUN_DOMAIN = System.getProperty("MAILGUN_DOMAIN");
    private static final String MAILGUN_API_KEY = System.getProperty("MAILGUN_API_KEY");

    @GetMapping("/dev/mail")
    public String showDevPageMail(Model model, HttpServletRequest request, HttpSession session) {
        return "dev/mail";
    }

    @PostMapping("/dev/mail/test")
    public String sendTestMail(@RequestParam String redirectUrl) {
        String recipient = "CMPT276 Realtorest <cmpt276projectgroup6+mailgun2@gmail.com>";
        String subject = "Test Mailgun API";
        String text = "This is a test email sent from the Realtorest application.\n"
            + "If you received this email, the Mailgun API is working correctly.";
        System.out.println("MAILGUN_DOMAIN: " + MAILGUN_DOMAIN);
        System.out.println("MAILGUN_API_KEY: " + MAILGUN_API_KEY);
        HttpResponse<JsonNode> response = Unirest
            .post("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN + "/messages")
            .basicAuth("api", MAILGUN_API_KEY)
            .field("from", "Mailgun Sandbox <postmaster@" + MAILGUN_DOMAIN + ">")
            .field("to", recipient)
            .field("subject", subject)
            .field("text", text)
            .asJson();
        System.out.println("Send Mail Response: " + response);
        return "redirect:" + redirectUrl;
    }
}
