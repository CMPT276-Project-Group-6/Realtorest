package cmpt276.pg6.realtorest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.Set; 

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PropertyRepository propertyRepository;


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
        String redirectUrl = "/"; // Usually we redirect to the home page

        // Perform POST request and verify redirection
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
            .param("username", username)
            .param("email", email)
            .param("password", password)
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

        // Drishty: im curious where are we redirecting to after login?
        // .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        // Kevin: The probably is likely because login have failed, since the email and password are not in the database.
    }

    @Test
    void testUserLogout() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        // Drishty: initially it should redirect to home page [/] not [""] whosoever was responsible for this function in model controller should fix it or either explain what is actually happening

        // Kevin: Both the home page and the empty string URL are the same page, but I changed it to redirect to "/" instead if you prefer that.
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
    public void testGetFavourites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        UserRepository userRepo = mock(UserRepository.class);
        User user = new User();
        user.setUid(1);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        MainController controller = new MainController();
        controller.setUserRepo(userRepo);
        
        // Act
        String result = controller.getFavourites(request, session, model);

        // Assert
        assertEquals("favourites", result);
        verify(model, times(1)).addAttribute(eq("favouriteProperties"), any(Set.class));
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testAddToFavourites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        UserRepository userRepo = mock(UserRepository.class);
        PropertyRepository propertyRepo = mock(PropertyRepository.class);
        User user = new User();
        user.setUid(1);
        Property property = new Property();
        property.setPid(1);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(propertyRepo.findById(1)).thenReturn(Optional.of(property));

        MainController controller = new MainController();
        controller.setUserRepo(userRepo);
        controller.setPropertyRepo(propertyRepo); // set the propertyRepo mock

        // Act
        ResponseEntity<String> result = controller.addToFavourites(1, request, session);

        // Assert
        assertEquals("Property added to favourites successfully", result.getBody());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    public void testRemoveFromFavourites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        UserRepository userRepo = mock(UserRepository.class);
        User user = new User();
        user.setUid(1);
        Property property = new Property();
        property.setPid(1);
        user.getFavouriteProperties().add(property);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        MainController controller = new MainController();
        controller.setUserRepo(userRepo);

        // Act
        ResponseEntity<String> result = controller.removeFromFavourites(1, request, session);

        // Assert
        assertEquals("Property removed from favourites successfully", result.getBody());
        verify(userRepo, times(1)).save(user);
    }
}



    


