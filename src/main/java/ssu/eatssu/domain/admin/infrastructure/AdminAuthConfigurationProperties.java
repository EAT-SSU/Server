package ssu.eatssu.domain.admin.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "admin")
public class AdminAuthConfigurationProperties {
	private String loginId;
}