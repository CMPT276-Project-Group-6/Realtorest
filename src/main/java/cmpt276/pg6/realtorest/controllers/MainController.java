package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PropertyRepository propertyRepo;
    @Autowired
    private AdminRepository adminRepo;

    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void setPropertyRepo(PropertyRepository propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/")
    public String showHomePage(Model model, HttpServletRequest request, HttpSession session) {
        // Check if the user is in the session
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        // Fetch the featured properties from the database
        List<Property> featuredProperties = propertyRepo.findByFeatured(true);
        model.addAttribute("properties", featuredProperties);
        // Display the home page
        return "home";
    }

    @GetMapping("/property-listing")
    public String showListingPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "propertyListing";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/login";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/register";
        } else {
            model.addAttribute("user", user);
            return "redirect:/";
        }
    }

    @GetMapping("/favourites")
    public String getFavourites(HttpServletRequest request, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Set<Property> favouriteProperties = user.getFavouriteProperties();
                model.addAttribute("favouriteProperties", favouriteProperties);
                model.addAttribute("user", user); // Add this line
                return "favourites";
            }
        }
        return "login";
    }

    // Dev Page for Properties Database
    @GetMapping("/dev/properties")
    public String showDevPageProperties(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "dev/properties";
    }

    // Dev Page for Admins Database
    @GetMapping("/dev/admins")
    public String showDevPageAdmins(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Admin> admins = adminRepo.findAll();
        model.addAttribute("admins", admins);
        return "dev/admins";
    }

    //Add property to favourites
    @PostMapping("/add-favourite/{propertyId}")
    public ResponseEntity<String> addToFavourites(@PathVariable Integer propertyId, HttpServletRequest request,
        HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            Property property = propertyRepo.findById(propertyId).orElse(null);

            if (user != null && property != null) {
                user.getFavouriteProperties().add(property);
                userRepo.save(user);
                return ResponseEntity.ok("Property added to favourites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User or Property not found");
    }

    //Remove property from favourites
    @DeleteMapping("/remove-favourite/{propertyId}")
    public ResponseEntity<String> removeFromFavourites(@PathVariable Integer propertyId, HttpServletRequest request,
        HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        Integer userId = sessionUser != null ? sessionUser.getUid() : null;

        if (userId != null) {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.getFavouriteProperties().removeIf(property -> property.getPid() == propertyId);
                userRepo.save(user);
                return ResponseEntity.ok("Property removed from favourites successfully");
            }
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    @PostMapping("/properties/add")
    public String addProperty(@RequestParam Map<String, String> newProperty, @RequestParam String redirectUrl,
        HttpServletResponse response) {
        String name = newProperty.get("name");
        String street = newProperty.get("street");
        String city = newProperty.get("city");
        String province = newProperty.get("province");
        String zipCode = newProperty.get("zipCode");
        String description = newProperty.get("description");
        int price = Integer.parseInt(newProperty.get("price"));
        double area = Double.parseDouble(newProperty.get("area"));
        int brCount = Integer.parseInt(newProperty.get("brCount"));
        int baCount = Integer.parseInt(newProperty.get("baCount"));
        boolean featured = Boolean.parseBoolean(newProperty.get("featured"));
        propertyRepo.save(
            new Property(name, street, city, province, zipCode, description, price, area, brCount, baCount, featured));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/properties/edit")
    public String editPropertyPage(Model model, @RequestParam int pid) {
        //TODO: process POST request
        Property property = propertyRepo.findById(pid).get();
        model.addAttribute("property", property);
        return "dev/edit-property";
    } //show edit property page

    @PostMapping("/properties/update/{pid}")
    public String updateProperty(@PathVariable int pid, @ModelAttribute Property Property) {
        Property updateProperty = propertyRepo.findById(pid).get();
        updateProperty = Property;
        propertyRepo.save(updateProperty);
        return "redirect:/dev/properties";
    }//updates Property info to db

    /**
     * Fills the properties database with testing data.
     */
    @PostMapping("/properties/fill")
    public String fillTestingDataProperties(@RequestParam String redirectUrl) {
        propertyRepo.save(new Property("Alice's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Bob's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Charlie's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("David's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Eve's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Frank's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Grace's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        propertyRepo.save(new Property("Heidi's House", "8888 University Dr", "Burnaby", "BC", "V5A 1S6",
            "Nothing much...", 1000000, 1500.50, 3, 2, false));
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes a property from the system.
     */
    @PostMapping("/properties/delete/{pid}")
    public String deleteProperty(@PathVariable int pid, @RequestParam String redirectUrl) {
        propertyRepo.deleteById(pid);
        return "redirect:" + redirectUrl;
    }

    /**
     * Deletes all properties from the database.
     * This is a dangerous operation and should not be used in a production environment.
     */
    @PostMapping("/properties/delete/all")
    public String deleteAllProperties(@RequestParam String redirectUrl) {
        propertyRepo.deleteAll();
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/admins/add")
    public String addAdmin(@RequestParam Map<String, String> newUser, @RequestParam String redirectUrl,
        HttpServletResponse response) {
        String adminName = newUser.get("adminname");
        String email = newUser.get("email");
        String password = newUser.get("password");
        adminRepo.save(new Admin(adminName, email, password));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/registerAdmin")
    public String showRegisterAdminPage(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "users/registerAdmin";
        } else {
            model.addAttribute("admin", admin);
            return "redirect:/";
        }
    }

    // Login Page for admin
    @GetMapping("/adminlogin")
    public String showAdminLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "users/adminlogin";
        } else {// Redirect to the home page if the user is already logged in
            model.addAttribute("admin", admin);
            return "protected";
        }
    }

    @PostMapping("/adminlogin")
    public String adminlogin(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
        HttpSession session) {
        // Process the login form (user enters email and password to login)
        String email = formData.get("email");
        String password = formData.get("password");
        List<Admin> adminList = adminRepo.findByEmailAndPassword(email, password);
        if (adminList.isEmpty()) {
            // If no user that matches the email and password is found, return to the login page
            // TODO Add a message to the login page that says "Invalid email or password"
            model.addAttribute("errorMessage", "Invalid Credentials Entered.");
            return "users/adminlogin";
        } else {
            // Successful login
            Admin admin = adminList.get(0);
            request.getSession().setAttribute("session_user", admin);
            model.addAttribute("admin", admin);
            return "protected";
        }
    }

    // lists all users in database for admin
    @GetMapping({"/listUsers"})
    public ModelAndView getAllUsers(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            throw new SecurityException("This is a protected page");
        } else {
            ModelAndView mav = new ModelAndView("list-users");
            mav.addObject("users", userRepo.findAll());
            return mav;
        }
    }
}
