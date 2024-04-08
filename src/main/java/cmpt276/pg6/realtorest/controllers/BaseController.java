package cmpt276.pg6.realtorest.controllers;

import org.springframework.ui.Model;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.User;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController {
    // Checks the session and adds the user or admin to the model attributes
    protected Object addModelAttributeFromSession(HttpSession session, Model model) {
        Object sessionUser = session.getAttribute("session_user");
        if (sessionUser instanceof User) {
            model.addAttribute("user", (User) sessionUser);
            return sessionUser;
        } else if (sessionUser instanceof Admin) {
            model.addAttribute("admin", (Admin) sessionUser);
            return sessionUser;
        }
        return null;
    }
}
