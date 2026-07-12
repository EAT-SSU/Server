package ssu.eatssu.domain.review.infrastructure;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeepLConfig {

    @Bean
    public RestTemplate deeplRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);
        factory.setHttpClient(client);
        return new RestTemplate(factory);
    }
}
