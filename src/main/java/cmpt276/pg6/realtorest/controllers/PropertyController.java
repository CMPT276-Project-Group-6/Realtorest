package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PropertyController {
    @Autowired
    private PropertyRepository propertyRepo;

    @Autowired
    private UserRepository userRepo;

    // Dev Page for Properties Database
    @GetMapping("/dev/properties")
    public String showDevPageProperties(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "dev/properties";
    }

    @GetMapping("/properties")
    public String getProperties(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String brCount,
        @RequestParam(required = false) String baCount,
        @RequestParam(required = false) String name,
        @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
        Model model) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "price");

        List<Property> properties;

        if (!city.isEmpty()) {
            properties = propertyRepo.findByCity(city, sort);
        } else if (!brCount.isEmpty() && brCount != null) {
            String numericPart = brCount.replaceAll("[^\\d]", "");
            int bathroomCount = Integer.parseInt(numericPart);
            properties = propertyRepo.findByBrCountGreaterThanEqual(bathroomCount, sort);
        } else if (!baCount.isEmpty()) {
            String numericPart = baCount.replaceAll("[^\\d]", "");
            int bedroomCount = Integer.parseInt(numericPart);

            properties = propertyRepo.findByBaCountGreaterThanEqual(bedroomCount, sort);
        } else if (name.isEmpty()) {
            properties = propertyRepo.findByNameContainingIgnoreCase(name, sort);
        } else {
            // Default action: fetch all listings sorted 
            properties = propertyRepo.findAll(sort);
        }
        model.addAttribute("city", city);
        model.addAttribute("brCount", brCount);
        model.addAttribute("baCount", baCount);
        model.addAttribute("name", name);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("properties", properties);
        System.out.println("Fetched properties: " + properties.size());
        return "propertyListing";
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

}
