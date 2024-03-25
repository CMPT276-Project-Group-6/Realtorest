package cmpt276.pg6.realtorest.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    MockHttpSession mockHttpSession;

    @MockBean
    private UserRepository userRepository;

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
    void testShowAdminLoginPage() throws Exception {
        // Setup session
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
            .andReturn().getRequest().getSession();

        // Perform GET request and verify the view name
        this.mockMvc.perform(MockMvcRequestBuilders.get("/adminlogin").session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("users/adminlogin"));
    }

    @Test
    void testShowAdminLoginPageLoggedIn() throws Exception {
        // Setup session with an admin user
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
            .andReturn().getRequest().getSession();
        session.setAttribute("session_user", new Admin());

        // Perform GET request and verify the view name
        this.mockMvc.perform(MockMvcRequestBuilders.get("/adminlogin").session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("protected"));
    }
}
