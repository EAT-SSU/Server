package ssu.eatssu;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "https://eatssu.shop",description = "Server url"),
@Server(url = "http://localhost:9000",description = "Local server")})
@EnableJpaAuditing
@SpringBootApplication
public class EatSsuApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatSsuApplication.class, args);
	}

}
