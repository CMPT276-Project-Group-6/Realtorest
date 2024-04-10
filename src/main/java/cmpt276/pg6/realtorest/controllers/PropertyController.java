package cmpt276.pg6.realtorest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
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
import cmpt276.pg6.realtorest.models.Image;
import cmpt276.pg6.realtorest.models.ImageRepository;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PropertyController extends BaseController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PropertyRepository propertyRepo;
    @Autowired
    private ImageRepository imageRepo;

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

    @GetMapping("/properties")
    public String getProperties(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String brCount,
        @RequestParam(required = false) String baCount,
        @RequestParam(required = false) String name,
        @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
        Model model, HttpSession session) {

        addModelAttributeFromSession(session, model);

        Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
        model.addAttribute("googleMapsApiKey", dotenv.get("GOOGLE_MAPS_API_KEY"));

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

        Map<Integer, List<Image>> propertyImages = new HashMap<>();
        for (Property property : properties) {
            List<Image> images = imageRepo.findByPropertyID(property.getPid());
            propertyImages.put(property.getPid(), images);
        }
        model.addAttribute("propertyImages", propertyImages);

        System.out.println("Fetched properties: " + properties.size());

        return "property-listings";
    }

    // Admin Page for Properties Database
    @GetMapping("/admin/properties")
    public String viewListing(Model model, HttpServletRequest request, HttpSession session) {
        Object currentUser = addModelAttributeFromSession(session, model);
        if (!(currentUser instanceof Admin)) {
            return "redirect:/admin/login";
        }

        // Get all properties from the database
        List<Property> properties = propertyRepo.findAll();
        model.addAttribute("properties", properties);
        return "admin/properties";
    }

    // Edit Listing for Admin
    @GetMapping("/properties/edit/{pid}")
    public String editListing(Model model, @PathVariable int pid, HttpSession session) {
        Object currentUser = addModelAttributeFromSession(session, model);
        if (!(currentUser instanceof Admin)) {
            return "redirect:/admin/login";
        }

        Property property = propertyRepo.findById(pid).get();
        model.addAttribute("property", property);
        return "admin/edit-property";
    }

    @PostMapping("/properties/update")
    public String updateProperty(@RequestParam("pid") int pid, @RequestParam("redirectUrl") String redirectUrl, @ModelAttribute Property Property) {
        Property updateProperty = propertyRepo.findById(pid).get();
        if (updateProperty == null) {
            return "redirect:" + redirectUrl;
        }
        updateProperty.setName(Property.getName());
        updateProperty.setStreet(Property.getStreet());
        updateProperty.setCity(Property.getCity());
        updateProperty.setProvince(Property.getProvince());
        updateProperty.setZipCode(Property.getZipCode());
        updateProperty.setDescription(Property.getDescription());
        updateProperty.setPrice(Property.getPrice());
        updateProperty.setArea(Property.getArea());
        updateProperty.setBrCount(Property.getBrCount());
        updateProperty.setBaCount(Property.getBaCount());
        updateProperty.setFeatured(Property.isFeatured());
        propertyRepo.save(updateProperty);
        return "redirect:" + redirectUrl;
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
        // Deletes all images that share the same property id
        List<Image> images = imageRepo.findByPropertyID(pid);
        for (Image image : images) {
            imageRepo.deleteById(image.getIid());
        }
        // Deletes all users that have the property in their favorites
        Iterable<User> users = userRepo.findAll();
        for (User user : users) {
            user.getFavoriteProperties().removeIf(property -> property.getPid() == pid);
            userRepo.save(user);
        }
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
