package cmpt276.pg6.realtorest.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    // private MainController mainController;

    @BeforeAll
    static void setUp() {
        // Grab the environment variables from the .env file
        Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
        System.setProperty("DB_REALTOREST_URL", dotenv.get("DB_REALTOREST_URL"));
        System.setProperty("DB_REALTOREST_USER", dotenv.get("DB_REALTOREST_USER"));
        System.setProperty("DB_REALTOREST_PASS", dotenv.get("DB_REALTOREST_PASS"));
    }

    @Test
    void testShowHomePage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("home"));
    }

    @Test
    void testShowLoginPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/login")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("users/login"));
    }
    @Test
    void testShowRegisterPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/register")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("users/register"));
    }
    @Test
    void testShowPropertyListingsPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/property-listings"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("propertyListings"));
    }

    @Test
    void testUserLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .param("email", "email@example.com")
        .param("password", "password12"))
        .andExpect(MockMvcResultMatchers.status().isAccepted())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    void testUserLogout() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }
    

    // @Test
    // void testAddProperty() throws Exception {
    //     // This is just an example
    //     this.mockMvc.perform(MockMvcRequestBuilders.post("/properties/add")
    //             .param("name", "Test Property")
    //             .param("location", "Test Location")
    //             .param("price", "100000")
    //             .param("bedrCount", "3")
    //             .param("bathrCount", "2"))
    //             .andExpect(MockMvcResultMatchers.status().isCreated());
    // }

    // modify this test after implementing the getAllProperties method and properties repository (database)

    // @Test
    // void testGetAllProperties() throws Exception {
    //     Property p1 = new Property();
    //     p1.setName("whatever fr now");
    //     p1.setLocation("whatever fr now");
    //     p1.setPrice(5000000);
    //     p1.setBedrCount(3);
    //     p1.setBathrCount(2);
    //     Property p2 = new Property();
    //     p2.setName("whatever 2");
    //     p2.setLocation("whatever 2");
    //     p2.setPrice(200000);
    //     p2.setBrCount(3);
    //     p2.setBaCount(1);

    //     ArrayList<Property> properties = new ArrayList<Property>();
    //     properties.add(p1);
    //     properties.add(p2);

    //     when(propertyRepository.findAll()).thenReturn(properties);
         

    //     this.mockMvc.perform(MockMvcRequestBuilders.get("/properties"))
    //     .andExpect(MockMvcResultMatchers.status().isOk());
    // }



}
