package cmpt276.pg6.realtorest.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;

@Controller
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepo;

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
            System.out.println("brCount: " + brCount);
            String numericPart = brCount.replaceAll("[^\\d]", "");
            int bathroomCount = Integer.parseInt(numericPart);
            properties = propertyRepo.findByBrCountGreaterThanEqual(bathroomCount, sort);
        } else if (!baCount.isEmpty()) {
            System.out.println("baCount: " + baCount);
            String numericPart = baCount.replaceAll("[^\\d]", "");
            System.out.println("Numeric part: " + numericPart);
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

}
