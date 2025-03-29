package ssu.eatssu.domain.admin.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ssu.eatssu.domain.admin.controller.AdminAuth;

@Configuration
@EnableConfigurationProperties(AdminAuthConfigurationProperties.class)
public class AdminConfiguration {

	@Bean
	public AdminAuth adminAuthConfigurationProperties(AdminAuthConfigurationProperties properties) {
		return new AdminAuth(properties.getLoginId());
	}
}
