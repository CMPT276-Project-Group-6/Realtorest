package cmpt276.pg6.realtorest.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.Property;
import cmpt276.pg6.realtorest.models.PropertyRepository;
import cmpt276.pg6.realtorest.models.User;
import cmpt276.pg6.realtorest.models.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllersTests {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    MockHttpSession mockHttpSession;

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
            .andExpect(MockMvcResultMatchers.view().name("user/login"));
    }

    @Test
    void testShowRegisterPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("user/register"));
    }

    @Test
    void testShowPropertyListingsPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/properties"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("property-listings"));
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

    // @Test
    // void testUserLogin() throws Exception {
    //     this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
    //         .param("email", "email@gmail.com")
    //         .param("password", "password12"))
    //         .andExpect(MockMvcResultMatchers.status().isOk());

    //     // Drishty: im curious where are we redirecting to after login?
    //     // .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    //     // Kevin: The probably is likely because login have failed, since the email and password are not in the database.
    // }

    @Test
    void testUserLogout() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        // Drishty: initially it should redirect to home page [/] not [""] whosoever was responsible for this function in model controller should fix it or either explain what is actually happening

        // Kevin: Both the home page and the empty string URL are the same page, but I changed it to redirect to "/" instead if you prefer that.
    }

    @Test
    public void testGetFavorites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        UserRepository userRepo = mock(UserRepository.class);
        User user = new User();
        user.setUid(1);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        FavoriteController favoriteController = new FavoriteController();
        favoriteController.setUserRepo(userRepo);

        String result = favoriteController.showFavoritesPage(request, session, model, response);

        // Assert
        assertEquals("favorites", result);
        verify(model, times(1)).addAttribute(eq("favoriteProperties"), any(Set.class));
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testAddToFavorites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        UserRepository userRepo = mock(UserRepository.class);
        PropertyRepository propertyRepo = mock(PropertyRepository.class);
        Model model = mock(Model.class);
        User user = new User();
        user.setUid(1);
        Property property = new Property();
        property.setPid(1);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(propertyRepo.findById(1)).thenReturn(Optional.of(property));

        FavoriteController favoriteController = new FavoriteController();
        favoriteController.setUserRepo(userRepo);
        favoriteController.setPropertyRepo(propertyRepo); // set the propertyRepo mock

        // Act
        ResponseEntity<String> result = favoriteController.addToFavorites(1, request, session, model);

        // Assert
        assertEquals("Property added to favorites successfully", result.getBody());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    public void testRemoveFromFavorites() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        UserRepository userRepo = mock(UserRepository.class);
        PropertyRepository propertyRepo = mock(PropertyRepository.class);
        Model model = mock(Model.class);
        User user = new User();
        user.setUid(1);
        Property property = new Property();
        property.setPid(1);
        user.getFavoriteProperties().add(property);
        when(session.getAttribute("session_user")).thenReturn(user);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        FavoriteController favoriteController = new FavoriteController();
        favoriteController.setUserRepo(userRepo);
        favoriteController.setPropertyRepo(propertyRepo); // set the propertyRepo mock

        // Act
        ResponseEntity<String> result = favoriteController.removeFromFavorites(1, request, session, model);

        // Assert
        assertEquals("Property removed from favorites successfully", result.getBody());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testShowAdminLoginPage() throws Exception {
        // Setup session
        MockHttpSession session =
            (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
                .andReturn().getRequest().getSession();

        // Perform GET request and verify the view name
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/login").session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("admin/login"));
    }

    @Test
    void testShowAdminLoginPageLoggedIn() throws Exception {
        // Setup session with an admin user
        MockHttpSession session =
            (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
                .andReturn().getRequest().getSession();
        session.setAttribute("session_user", new Admin());

        // Perform GET request and verify the view name
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/login").session(session))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/admin"));
    }

    // Kevin: Seems like the list all users functionality for admin is missing. Disabling the test for now.

    // @Test
    // void testGetAllUsers() throws Exception {
    //     // Setup session with an admin user
    //     MockHttpSession session =
    //         (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.get("/").session(mockHttpSession))
    //             .andReturn().getRequest().getSession();
    //     session.setAttribute("session_user", new Admin());

    //     // Mock the user repository and its findAll() method
    //     List<User> users = new ArrayList<>();
    //     users.add(new User("user1", "user1@example.com", "password1"));
    //     users.add(new User("user2", "user2@example.com", "password2"));
    //     when(userRepository.findAll()).thenReturn(users);

    //     // Perform GET request and verify the view name and model attribute
    //     this.mockMvc.perform(MockMvcRequestBuilders.get("/listUsers").session(session))
    //         .andExpect(MockMvcResultMatchers.status().isOk())
    //         .andExpect(MockMvcResultMatchers.view().name("list-users"))
    //         .andExpect(MockMvcResultMatchers.model().attributeExists("users"))
    //         .andExpect(MockMvcResultMatchers.model().attribute("users", users));
    // }

    // @Test
    // void testGetAllUsersUnauthenticated() throws Exception {
    //     // Perform GET request without an admin user in the session
    //     Exception exception = assertThrows(jakarta.servlet.ServletException.class, () -> {
    //         this.mockMvc.perform(MockMvcRequestBuilders.get("/listUsers"))
    //             .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    //             .andExpect(MockMvcResultMatchers.handler().handlerType(MainController.class))
    //             .andExpect(MockMvcResultMatchers.handler().methodName("getAllUsers"));
    //     });
    //     assertEquals("Request processing failed: java.lang.SecurityException: This is a protected page",
    //         exception.getMessage());
    // }
}
