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
        return "admin/properties-edit";
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
        return "dev/properties-edit";
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
        Property thisOne;

        thisOne = propertyRepo.save(new Property(
            "Alice's House",
            "262 Cedar Lane",
            "Vancouver",
            "BC",
            "V5Y 4R2",
            "Discover serenity in this modern duplex on Cedar Lane. Featuring a private backyard and upscale finishes, it offers the perfect blend of comfort and style. With three bedrooms and an open-concept living area, there's plenty of space for family gatherings and relaxation. Conveniently located near parks, schools, and transit routes.",
            9000000,
            4945.0,
            2,
            1,
            false));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property1/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property1/Livingroom.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property1/Bathroom.jpg"));

        thisOne = propertyRepo.save(new Property(
            "Bob's House",
            "13579 Pine Road",
            "Vancouver",
            "BC",
            "V6P 3V2",
            "Experience contemporary living in this sleek apartment on Pine Road. Offering floor-to-ceiling windows and a gourmet kitchen, it's perfect for urban professionals. Enjoy access to onsite amenities including a fitness center and rooftop garden, all within close proximity to transportation and entertainment options.",
            10000000,
            4000.0,
            3,
            2,
            false));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property2/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property2/Kitchen.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property2/Washroom.jpg"));

        thisOne = propertyRepo.save(new Property(
            "Charlie's House",
            "1234 Main Street",
            "Vancouver",
            "BC",
            "V6A 1B6",
            "This cozy one-bedroom apartment on Main Street boasts modern amenities with an open-concept layout. Enjoy stunning city views from the balcony, perfect for relaxing evenings. Located in the heart of Vancouver, it offers easy access to trendy cafes, restaurants, and parks.",
            2000000,
            4000.0,
            1,
            2,
            false));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property3/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property3/Kitchen.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property3/Washroom.jpg"));

        thisOne = propertyRepo.save(new Property(
            "David's House",
            "8203 Elm Street",
            "Vancouver",
            "BC",
            "V6M 2Y6",
            "This spacious condominium on Elm Street features modern design and abundant natural light. With two bedrooms and a den, there's plenty of space for a home office or guest room. Located in a vibrant neighborhood, it's within walking distance to shops, dining, and recreational facilities.",
            3000000,
            3200.0,
            3,
            3,
            true));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property4/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property4/Kitchen.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property4/Washroom.jpg"));

        thisOne = propertyRepo.save(new Property(
            "Eve's House",
            "9876 Maple Drive",
            "Vancouver",
            "BC",
            "V7S 1Z8",
            "Embrace luxury living in this elegant townhouse on Maple Drive. Offering four bedrooms, a gourmet kitchen, and high-end finishes throughout, it exudes sophistication. Enjoy breathtaking mountain views from the rooftop terrace, ideal for entertaining guests or unwinding after a long day.",
            4000000,
            2500.0,
            2,
            3,
            false));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property5/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property5/Kitchen.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property5/Washroom.jpg"));

        thisOne = propertyRepo.save(new Property(
            "Frank's House",
            "567 Oak Avenue",
            "Vancouver",
            "BC",
            "V5Z 2W9",
            "Nestled in a quiet neighborhood, this charming two-story home on Oak Avenue features three bedrooms, ideal for a growing family. With a spacious backyard and deck, it's perfect for outdoor gatherings and BBQs. Conveniently located near schools, shopping centers, and public transit.",
            2500000,
            2000.0,
            1,
            1,
            true));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property6/Front.jpg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property6/Kitchen.jpeg"));
        imageRepo.save(new Image(thisOne.getPid(), "https://raw.githubusercontent.com/CMPT276-Project-Group-6/Realtorest/main/Images/Property6/Washroom.jpg"));

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
