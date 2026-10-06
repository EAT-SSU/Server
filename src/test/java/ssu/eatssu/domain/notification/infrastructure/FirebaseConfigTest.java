package ssu.eatssu.domain.notification.infrastructure;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FirebaseConfig.class));

    @AfterEach
    void tearDown() {
        FirebaseApp.getApps().stream()
                   .filter(app -> FirebaseConfig.APP_NAME.equals(app.getName()))
                   .forEach(FirebaseApp::delete);
    }

    @Test
    void createsFirebaseMessagingFromBase64ServiceAccountAndReusesTheApp() throws Exception {
        // given
        String credentials = encodedServiceAccount();
        FirebaseConfig config = new FirebaseConfig();

        // when
        FirebaseMessaging first = config.firebaseMessaging(credentials);
        FirebaseMessaging second = config.firebaseMessaging(" " + credentials + "\n");

        // then
        assertThat(first).isSameAs(second);
        assertThat(FirebaseApp.getApps()).extracting(FirebaseApp::getName)
                                         .containsOnlyOnce(FirebaseConfig.APP_NAME);
    }

    @Test
    void registersFirebaseMessagingBeanOnlyWhenCredentialsExist() throws Exception {
        // given
        String credentials = encodedServiceAccount();

        // when & then
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(FirebaseMessaging.class));
        contextRunner.withPropertyValues("firebase.credentials=")
                     .run(context -> assertThat(context).doesNotHaveBean(FirebaseMessaging.class));
        contextRunner.withPropertyValues("firebase.credentials=" + credentials)
                     .run(context -> assertThat(context).hasSingleBean(FirebaseMessaging.class));
    }

    private String encodedServiceAccount() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        PrivateKey privateKey = generator.generateKeyPair().getPrivate();
        String pem = "-----BEGIN PRIVATE KEY-----\\n"
                + Base64.getEncoder().encodeToString(privateKey.getEncoded())
                + "\\n-----END PRIVATE KEY-----\\n";
        String json = """
                {
                  "type": "service_account",
                  "project_id": "test-project",
                  "private_key_id": "key-id",
                  "private_key": "%s",
                  "client_email": "firebase-adminsdk@test-project.iam.gserviceaccount.com",
                  "client_id": "1",
                  "token_uri": "https://oauth2.googleapis.com/token"
                }
                """.formatted(pem);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
