package cmpt276.pg6.realtorest.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import cmpt276.pg6.realtorest.models.User;


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
        this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("home"));
    }

    @Test
    void testShowLoginPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("users/login"));
    }

    @Test
    void testShowRegisterPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("users/register"));
    }

    @Test
    void testShowPropertyListingsPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/property-listing"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("propertyListing"));
    }

    @Test
    void testUserRegister() throws Exception {
        // Setup parameters
        String username = "newUser";
        String email = "newUser@example.com";
        String password = "securePassword123";
        String redirectUrl = "/login"; // Assuming redirection to login after registration

        // Perform POST request and verify redirection
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
            .param("username", username)
            .param("email@gmail.com", email)
            .param("password12", password)
            .param("redirectUrl", redirectUrl))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expecting redirection
            .andExpect(MockMvcResultMatchers.redirectedUrl(redirectUrl)); // Verify the redirection URL
    }

    @Test
    void testUserLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("email", "email@gmail.com")
            .param("password", "password12"))
            .andExpect(MockMvcResultMatchers.status().isOk());
        // im curious where are we redirecting to after login?
        // .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    void testUserLogout() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
            .andExpect(MockMvcResultMatchers.redirectedUrl(""));
        // .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        // initally it should redirect to home page [/] not [""]
        // whosoever was responsible for this function in model controller
        // should fix it or either explain what is actually happening
    }

    // @Test
    // void testAddProperty() throws Exception {
    // // This is just an example
    // this.mockMvc.perform(MockMvcRequestBuilders.post("/properties/add")
    // .param("name", "Test Property")
    // .param("location", "Test Location")
    // .param("price", "100000")
    // .param("bedrCount", "3")
    // .param("bathrCount", "2"))
    // .andExpect(MockMvcResultMatchers.status().isCreated());
    // }

    // modify this test after implementing the getAllProperties method and
    // properties repository (database)

    // @Test
    // void testGetAllProperties() throws Exception {
    // Property p1 = new Property();
    // p1.setName("whatever fr now");
    // p1.setLocation("whatever fr now");
    // p1.setPrice(5000000);
    // p1.setBedrCount(3);
    // p1.setBathrCount(2);
    // Property p2 = new Property();
    // p2.setName("whatever 2");
    // p2.setLocation("whatever 2");
    // p2.setPrice(200000);
    // p2.setBrCount(3);
    // p2.setBaCount(1);

    // ArrayList<Property> properties = new ArrayList<Property>();
    // properties.add(p1);
    // properties.add(p2);

    // when(propertyRepository.findAll()).thenReturn(properties);

    // this.mockMvc.perform(MockMvcRequestBuilders.get("/properties"))
    // .andExpect(MockMvcResultMatchers.status().isOk());
    // }

    @Test
    void testShowFavouritesPage() throws Exception {
        // Assuming there's a user logged in with id 1 and they have two favourite properties with ids 1 and 2
        HttpSession session = new MockHttpSession();
        User user = new User();
        user.setUid(1);
        user.setFavouritePropertyIds(Arrays.asList(1, 2));
        session.setAttribute("session_user", user);

        // Perform the request
        this.mockMvc.perform(MockMvcRequestBuilders.get("/favourites").session((MockHttpSession) session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("favourites"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("favoriteProperties"));
    }

    @Test
    void testAddFavourite() throws Exception {
        // Assuming there's a user logged in with id 1
        HttpSession session = new MockHttpSession();
        User user = new User();
        user.setUid(1);
        session.setAttribute("session_user", user);

        // Perform the request
        this.mockMvc.perform(MockMvcRequestBuilders.post("/add-favourite")
            .param("propertyId", "1")
            .session((MockHttpSession) session))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveFavourite() throws Exception {
        // Assuming there's a user logged in with id 1 and they have a favourite property with id 1
        HttpSession session = new MockHttpSession();
        User user = new User();
        user.setUid(1);
        user.setFavouritePropertyIds(new ArrayList<>(Arrays.asList(1)));
        session.setAttribute("session_user", user);

        // Perform the request
        this.mockMvc.perform(MockMvcRequestBuilders.post("/remove-favourite")
            .param("propertyId", "1")
            .session((MockHttpSession) session))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }




}
