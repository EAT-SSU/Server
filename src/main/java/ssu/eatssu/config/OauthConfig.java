package ssu.eatssu.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OauthConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).build(); //5s
        /*HttpClient client = HttpClientBuilder.create()
                .setMaxConnTotal(50) //연결을 유지할 최대 숫자
                .setMaxConnPerRoute(20) // 특정 경로당 최대 숫자
                .setDefaultSocketConfig(socketConfig)
                .build();*/
        CloseableHttpClient client = HttpClientBuilder.create().build();
        factory.setConnectTimeout(3000); //3s
        factory.setHttpClient(client);
        return new RestTemplate(factory);
    }
}
