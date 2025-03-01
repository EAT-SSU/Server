package ssu.eatssu.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String API_NAME = "EAT-SSU API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "EAT-SSU API 명세서";

    @Value("${swagger.url}")
    private String SERVER_URL;
    @Value("${swagger.description}")
    private String SERVER_DESCRIPTION;


    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version(API_VERSION)
                .title(API_NAME)
                .description(API_DESCRIPTION);

        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url(SERVER_URL).description(SERVER_DESCRIPTION));

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(Arrays.asList(securityRequirement))
                .servers(servers)
                .info(info);
    }
}