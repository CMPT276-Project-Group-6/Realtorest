package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cmpt276.pg6.realtorest.models.User;
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
            .map(user -> user.getFirstName() + " " + user.getLastName() + " <" + user.getEmail() + ">")
            .toArray(String[]::new);
    }

    // Method for sending password reset email
    @PostMapping("/forgot-password")
    public String sendPasswordResetEmail(@RequestParam String email, @RequestParam String confirmEmail, HttpServletRequest request) {
        // Find the user by email
        List<User> users = userRepo.findByEmail(email);
        User user = users.isEmpty() ? null : users.get(0);
        if (user == null) {
            // Handle case where there's no user with this email
            return "redirect:/register";
        }

        // Generate a unique reset token
        String resetToken = generateResetToken(user);

        // Construct the base URL of your application
        String appUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            appUrl += ":" + request.getServerPort();
        }

        // Construct the reset password link with the reset token and email
        String resetLink = appUrl + "/reset-password?token=" + resetToken + "&email=" + email;

        // Construct the email content
        String recipient = email;
        String subject = "Password Reset Request";
        String text = "Use the link below to reset your password:<br/><a href=\"" + resetLink
            + "\" target=\"_blank\" style=\"color: blue; text-decoration: underline;\">" + resetLink + "</a>";

        // Send the email using MailGun API
        HttpResponse<JsonNode> response = Unirest
            .post("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN + "/messages")
            .basicAuth("api", MAILGUN_API_KEY)
            .field("from", "Mailgun Sandbox <postmaster@" + MAILGUN_DOMAIN + ">")
            .field("to", recipient)
            .field("subject", subject)
            .field("html", text) // Use 'html' field instead of 'text'
            .asJson();

        // Check if the email was sent successfully
        if (response.getStatus() == 200) {
            System.out.println("Password reset email sent successfully!");
        } else {
            System.out.println("Failed to send password reset email. Status code: " + response.getStatus());
        }

        // Redirect user to login page
        return "redirect:/login";
    }

    // Generate a unique reset token
    private String generateResetToken(User user) {
        String resetToken = UUID.randomUUID().toString();

        // Save the reset token in the user's record
        user.setResetToken(resetToken);
        userRepo.save(user);

        return resetToken;
    }

    @PostMapping("/popup/send")
    public ResponseEntity<String> handlePopupFormSubmission(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("phone") String phone) {
        String recipient = "CMPT276 Realtorest <cmpt276projectgroup6@gmail.com>"; //Change email to whoever want to send Popup info to
        String subject = "New contact form submission from " + name;
        String text = "Name: " + name + "\nEmail: " + email + "\nPhone: " + phone;

        HttpResponse<JsonNode> response = sendMail(recipient, subject, text);
        System.out.println("Send Mail Response: " + response.getBody().toPrettyString());

        return new ResponseEntity<>("Form submitted successfully", HttpStatus.OK);
    }

}
