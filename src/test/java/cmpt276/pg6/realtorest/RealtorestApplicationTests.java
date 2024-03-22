package cmpt276.pg6.realtorest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class RealtorestApplicationTests {
	@BeforeAll
	static void setUp() {
		// Grab the environment variables from the .env file
		Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
		System.setProperty("DB_REALTOREST_URL", dotenv.get("DB_REALTOREST_URL"));
		System.setProperty("DB_REALTOREST_USER", dotenv.get("DB_REALTOREST_USER"));
		System.setProperty("DB_REALTOREST_PASS", dotenv.get("DB_REALTOREST_PASS"));
	}

	@Test
	void contextLoads() {}
}
