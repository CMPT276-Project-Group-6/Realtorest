package cmpt276.pg6.realtorest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kong.unirest.core.Unirest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;

@Controller
public class MailgunController {
    @GetMapping("/dev/mail")
    public String showDevPageMail(Model model, HttpServletRequest request, HttpSession session) {
        return "dev/mail";
    }

    /**
     * 
     */
    @PostMapping("/dev/mail/test")
    public String sendTestMail(@RequestParam String redirectUrl) {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost/post")
            .header("accept", "application/json")
            .queryString("apiKey", "123")
            .field("parameter", "value")
            .field("firstname", "Gary")
            .asJson();
        return "redirect:" + redirectUrl;
    }
}
