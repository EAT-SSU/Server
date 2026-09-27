package ssu.eatssu.domain.notification.infrastructure;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    static final String APP_NAME = "eatssu";

    @Bean
    @ConditionalOnExpression("!'${firebase.credentials:}'.isBlank()")
    public FirebaseMessaging firebaseMessaging(@Value("${firebase.credentials}") String encodedCredentials)
            throws IOException {
        byte[] credentialsJson = Base64.getDecoder().decode(encodedCredentials.strip());
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson));
        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(credentials)
                                                 .setHttpTransport(new NetHttpTransport())
                                                 .build();
        return FirebaseMessaging.getInstance(getOrInitializeApp(options));
    }

    private FirebaseApp getOrInitializeApp(FirebaseOptions options) {
        return FirebaseApp.getApps().stream()
                          .filter(app -> APP_NAME.equals(app.getName()))
                          .findFirst()
                          .orElseGet(() -> FirebaseApp.initializeApp(options, APP_NAME));
    }
}
