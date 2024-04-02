package cmpt276.pg6.realtorest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class RealtorestApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().directory("./etc/secrets").load();
		System.setProperty("DB_REALTOREST_URL", dotenv.get("DB_REALTOREST_URL"));
		System.setProperty("DB_REALTOREST_USER", dotenv.get("DB_REALTOREST_USER"));
		System.setProperty("DB_REALTOREST_PASS", dotenv.get("DB_REALTOREST_PASS"));
		System.setProperty("MAILGUN_DOMAIN", dotenv.get("MAILGUN_DOMAIN"));
		System.setProperty("MAILGUN_API_KEY", dotenv.get("MAILGUN_API_KEY"));
		SpringApplication.run(RealtorestApplication.class, args);
	}
}
