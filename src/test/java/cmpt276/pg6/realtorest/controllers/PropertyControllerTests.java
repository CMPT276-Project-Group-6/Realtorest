package cmpt276.pg6.realtorest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
public class PropertyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static PropertyRepository propertyRepository;

    @BeforeAll
    static void setUp() {
        // Grab the environment variables from the .env file
        Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
        System.setProperty("DB_REALTOREST_URL", dotenv.get("DB_REALTOREST_URL"));
        System.setProperty("DB_REALTOREST_USER", dotenv.get("DB_REALTOREST_USER"));
        System.setProperty("DB_REALTOREST_PASS", dotenv.get("DB_REALTOREST_PASS"));
    }

    // Sample test to ensure context loads and mockMvc is injected
    @Test
    void testContextLoadsPropertyListingsPage() throws Exception {
        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk());
    }

    // Get properties page with no parameters
    @Test
    void testGetProperties_NoParams_ShouldReturnAllProperties() throws Exception {
        // Setup mock behavior
        when(propertyRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk())
            .andExpect(view().name("property-listings"))
            .andExpect(model().attributeExists("properties"));

        verify(propertyRepository, times(1)).findAll(any(Sort.class));
    }

    // Admin access to edit listing, Should Return EditPage
    @Test
    void testEditListings_Admin() throws Exception {
        mockMvc.perform(get("/admin/properties")
            .sessionAttr("session_user", new Admin()))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/properties"));
    }

    // test for addProperty POST method
    @Test
    public void testAddProperty_ShouldSavePropertyAndRedirect() throws Exception {
        // Define property parameters
        String redirectUrl = "/properties";
        mockMvc.perform(post("/properties/add")
            .param("name", "Test Property")
            .param("street", "1234 Test St")
            .param("city", "Testville")
            .param("province", "Test Province")
            .param("zipCode", "123456")
            .param("description", "A test property")
            .param("price", "100000")
            .param("area", "123.45")
            .param("brCount", "3")
            .param("baCount", "2")
            .param("featured", "true")
            .param("redirectUrl", redirectUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));
        // Verify that the property repository's save method was called once
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    // Test to check delete property, Should delete and redirect
    @Test
    void testDeleteProperty() throws Exception {
        int pid = 1; // Example property ID
        String redirectUrl = "/properties";

        mockMvc.perform(post("/properties/delete/{pid}", pid).param("redirectUrl", redirectUrl))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));

        verify(propertyRepository, times(1)).deleteById(pid);
    }

    // Tests for developer pages, temporarily disabled
    // test to show dev page properties, Should return all properties
    @Test
    void testShowDevPageProperties() throws Exception {
        when(propertyRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/dev/properties"))
            .andExpect(status().isOk())
            .andExpect(view().name("dev/properties"))
            .andExpect(model().attributeExists("properties"));

        verify(propertyRepository, times(1)).findAll();
    }

    // test to show DevEditProperty, Should return edit property page
    @Test
    void testShowDevEditProperty() throws Exception {
        int pid = 1; // Example property ID
        Property property = new Property(); // Mock property (fill with data as needed)
        when(propertyRepository.findById(pid)).thenReturn(java.util.Optional.of(property));

        mockMvc.perform(get("/dev/properties/edit").param("pid", String.valueOf(pid)))
            .andExpect(status().isOk())
            .andExpect(view().name("dev/properties-edit"))
            .andExpect(model().attributeExists("property"));

        verify(propertyRepository, times(1)).findById(pid);
    }

    // test to check delete all properties 
    @Test
    void testDeleteAllProperties_ShouldDeleteAllAndRedirect() throws Exception {
        String redirectUrl = "/properties";

        mockMvc.perform(post("/properties/delete/all").param("redirectUrl", redirectUrl))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));
        verify(propertyRepository, times(1)).deleteAll();
    }
}
