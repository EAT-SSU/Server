package ssu.eatssu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EatSsuApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatSsuApplication.class, args);
	}

}
