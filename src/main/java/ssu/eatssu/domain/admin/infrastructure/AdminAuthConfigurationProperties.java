package ssu.eatssu.domain.admin.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "admin")
public class AdminAuthConfigurationProperties {
    private String loginId;
}