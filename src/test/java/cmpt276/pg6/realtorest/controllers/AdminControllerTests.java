package cmpt276.pg6.realtorest.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import cmpt276.pg6.realtorest.models.Admin;
import cmpt276.pg6.realtorest.models.AdminRepository;
import io.github.cdimascio.dotenv.Dotenv;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminRepository adminRepository;

    private static Admin admin;
    
    @BeforeAll
    static void setUp() {
        // Grab the environment variables from the .env file
        Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
        System.setProperty("DB_REALTOREST_URL", dotenv.get("DB_REALTOREST_URL"));
        System.setProperty("DB_REALTOREST_USER", dotenv.get("DB_REALTOREST_USER"));
        System.setProperty("DB_REALTOREST_PASS", dotenv.get("DB_REALTOREST_PASS"));

        admin = new Admin("AdminName", "admin@example.com", "password");
    }

    

    @Test
    public void showAdminLoginPage_NotLoggedIn_ShouldReturnLoginPage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/login"))
        .andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.view().name("admin/login"));
    }



    @Test
    public void adminLogin_WithValidCredentials_ShouldRedirectToAdminHomepage() throws Exception {
        when(adminRepository.findByEmailAndPassword("admin@example.com", "password")).thenReturn(List.of(admin));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/login")
        .param("email", "admin@example.com")
        .param("password", "password"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin"));
        
        verify(adminRepository, times(1)).findByEmailAndPassword("admin@example.com", "password");
    }

    @Test
    public void adminLogin_WithInvalidCredentials_ShouldReturnToLogin() throws Exception {
        when(adminRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(List.of());

        mockMvc.perform(post("/admin/login").param("email", "wrong@example.com").param("password", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Invalid Credentials Entered."));
    }

    @Test
    public void addAdmin_ShouldCreateAdminAndRedirect() throws Exception {
        mockMvc.perform(post("/admins/add")
                .param("adminname", "NewAdmin")
                .param("email", "newadmin@example.com")
                .param("password", "newPassword")
                .param("redirectUrl", "/dev/admins"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dev/admins"));
        
        verify(adminRepository, times(1)).save(any(Admin.class));
    }
}
    

