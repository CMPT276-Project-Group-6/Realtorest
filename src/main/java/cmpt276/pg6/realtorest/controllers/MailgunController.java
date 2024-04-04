package cmpt276.pg6.realtorest.controllers;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

/**
 * This is for stuff related to Mailgun
 */
@Controller
public class MailgunController {
    @Autowired
    private UserRepository userRepo;

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private static final String MAILGUN_DOMAIN = System.getProperty("MAILGUN_DOMAIN");
    private static final String MAILGUN_API_KEY = System.getProperty("MAILGUN_API_KEY");
    // private static final String[] RECIPIENTS = {
    //         "Test 1 <cmpt276projectgroup6+test1@gmail.com>",
    //         "Test 2 <cmpt276projectgroup6+test2@gmail.com>"
    // };

    @GetMapping("/dev/mail")
    public String showDevPageMail(Model model, HttpServletRequest request, HttpSession session) {
        return "dev/mail";
    }

    @PostMapping("/dev/mail/test")
    public String sendTestMail(@RequestParam String redirectUrl) {
        String recipient = "CMPT276 Realtorest <cmpt276projectgroup6+test1@gmail.com>";
        String subject = "Test Mailgun API";
        String text = "This is a test email sent from the Realtorest application.\n"
            + "If you received this email, the Mailgun API is working correctly.";
        // System.out.println("MAILGUN_DOMAIN: " + MAILGUN_DOMAIN);
        // System.out.println("MAILGUN_API_KEY: " + MAILGUN_API_KEY);

        HttpResponse<JsonNode> response = sendMail(recipient, subject, text);
        System.out.println("Send Mail Response: " + response.getBody().toPrettyString());
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/mail/send")
    public String sendMailsToMailinglist(@RequestParam Map<String, String> mail, @RequestParam String redirectUrl) {
        String[] recipients = loadRecipients();

        String subject = mail.get("subject");
        String text = mail.get("text");
        for (String recipient : recipients) {
            HttpResponse<JsonNode> response = sendMail(recipient, subject, text);
            System.out.println("Send Mail Response: " + response.getBody().toPrettyString());
        }
        return "redirect:" + redirectUrl;
    }

    // Send an email using the Mailgun API
    private HttpResponse<JsonNode> sendMail(String recipient, String subject, String text) {
        // This is Kevin's bad habit of writing functions with a single return statement lol
        return Unirest
            .post("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN + "/messages")
            .basicAuth("api", MAILGUN_API_KEY)
            .field("from", "Mailgun Sandbox <postmaster@" + MAILGUN_DOMAIN + ">")
            .field("to", recipient)
            .field("subject", subject)
            .field("text", text)
            .asJson();
    }

    private String[] loadRecipients() {
        return userRepo.findByIsOnMailingList(true).stream()
            .map(user -> user.getUsername() + " <" + user.getEmail() + ">")
            .toArray(String[]::new);
    }

}
