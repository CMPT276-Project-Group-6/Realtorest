package cmpt276.pg6.realtorest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PropertyController {
    @Autowired
    private PropertyRepository propertyRepo;

    public void setPropertyRepo(PropertyRepository propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    /**
     * Grabs the current URL and stores it as a model attribute, which means everything can use it. Mostly used for refreshing the page.
     * Kevin: Note that this is something that is used in every controller, but I don't know how to extract this.
     */
    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // @GetMapping("/property-listing")
    // public String showListingPage(Model model, HttpServletRequest request, HttpSession session) {
    //     User user = (User) session.getAttribute("session_user");
    //     if (user != null) {
    //         model.addAttribute("user", user);
    //     }
    //     List<Property> properties = propertyRepo.findAll();
    //     model.addAttribute("properties", properties);
    //     return "propertyListing";
    // }

    @GetMapping("/properties")
    public String getProperties(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String brCount,
        @RequestParam(required = false) String baCount,
        @RequestParam(required = false) String name,
        @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
        Model model) {

        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "price");

        boolean cityPresent = city != null && !city.isEmpty();
        boolean brCountPresent = brCount != null && !brCount.isEmpty();
        boolean baCountPresent = baCount != null && !baCount.isEmpty();
        boolean namePresent = name != null && !name.isEmpty();

        List<Property> properties = new ArrayList<>();

        if (cityPresent || brCountPresent || baCountPresent || namePresent) {
            // Initially, fetch all listings sorted as a base case
            properties = propertyRepo.findAll(sort);

            if (cityPresent) {
                properties = propertyRepo.findByCity(city, sort);
            }

            if (brCountPresent) {
                String numericPartBr = brCount.replaceAll("[^\\d]", "");
                int bedroomCount = Integer.parseInt(numericPartBr);
                properties = propertyRepo.findByBrCountGreaterThanEqual(bedroomCount, sort);
            }

            if (baCountPresent) {
                String numericPartBa = baCount.replaceAll("[^\\d]", "");
                int bathroomCount = Integer.parseInt(numericPartBa);
                properties = propertyRepo.findByBaCountGreaterThanEqual(bathroomCount, sort);
            }

            if (namePresent) {
                properties = propertyRepo.findByNameContainingIgnoreCase(name, sort);
            }
        } else {
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
    
    // Admin Page for Properties Database
    // Edit Listing for Admin
    @GetMapping("/admin/properties")
    public String editListing(Model model, HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        // Get all properties from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        model.addAttribute("admin", admin);
        return "admin/properties";
    }

    // Dev Page for Properties Database
    @GetMapping("/dev/properties")
    public String showDevPageProperties(Model model, HttpServletRequest request, HttpSession session) {
        // Get all users from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "dev/properties";
    }

    // Show edit property page
    @GetMapping("/dev/properties/edit")
    public String showDevEditProperty(Model model, @RequestParam int pid) {
        Property property = propertyRepo.findById(pid).get();
        model.addAttribute("property", property);
        return "dev/edit-property";
    }

    @PostMapping("/properties/add")
    public String addProperty(@RequestParam Map<String, String> newProperty, @RequestParam String redirectUrl, HttpServletResponse response) {
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
