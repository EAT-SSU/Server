package ssu.eatssu.domain.review.infrastructure;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeepLConfig {

    private static final int MAX_CONN_PER_ROUTE = 15;
    private static final int MAX_CONN_TOTAL = 15;
    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int CONNECTION_REQUEST_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 3000;

    @Bean
    public RestTemplate deeplRestTemplate() {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                                                                                                          .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                                                                                                          .setMaxConnTotal(MAX_CONN_TOTAL)
                                                                                                          .build();
        CloseableHttpClient client = HttpClientBuilder.create()
                                                        .setConnectionManager(connectionManager)
                                                        .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        return new RestTemplate(factory);
    }
}
