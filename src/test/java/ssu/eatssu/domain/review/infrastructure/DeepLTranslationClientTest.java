package ssu.eatssu.domain.review.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

class DeepLTranslationClientTest {

    private RestTemplate restTemplate;
    private DeepLTranslationClient client;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        client = new DeepLTranslationClient(restTemplate);
        ReflectionTestUtils.setField(client, "apiKey", "test-key");
        ReflectionTestUtils.setField(client, "baseUrl", "https://deepl.test");
    }

    @Test
    void returnsFirstTranslationText() {
        given(restTemplate.postForObject(eq("https://deepl.test/v2/translate"), any(),
                eq(DeepLTranslateResponse.class)))
                .willReturn(new DeepLTranslateResponse(List.of(new DeepLTranslateResponse.Translation("KO", "Hello"))));

        assertThat(client.translate("안녕", Language.EN)).isEqualTo("Hello");
    }

    @Test
    void rejectsMissingTranslationAndMapsClientErrors() {
        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willReturn(null);
        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);

        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willThrow(HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "quota", null, null, null));
        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);

        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willThrow(new ResourceAccessException("timeout"));
        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);

        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willThrow(new RestClientException("failed"));
        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);
    }

    @Test
    void rejectsWhenTranslationsListIsEmpty() {
        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willReturn(new DeepLTranslateResponse(List.of()));

        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);
    }

    @Test
    void mapsQuotaExceededStatus456ToQuotaException() {
        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willThrow(HttpClientErrorException.create(HttpStatusCode.valueOf(456), "quota", null, null, null));

        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);
    }

    @Test
    void mapsHttpServerErrorExceptionToTranslationFailed() {
        given(restTemplate.postForObject(any(String.class), any(), eq(DeepLTranslateResponse.class)))
                .willThrow(HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "error", null, null, null));

        assertThatThrownBy(() -> client.translate("안녕", Language.EN)).isInstanceOf(BaseException.class);
    }

    @Test
    void exposesTranslationRecordValues() {
        DeepLTranslateResponse.Translation translation =
                new DeepLTranslateResponse.Translation("JA", "こんにちは");
        DeepLTranslateResponse response = new DeepLTranslateResponse(List.of(translation));

        assertThat(response.translations()).containsExactly(translation);
        assertThat(translation.detectedSourceLanguage()).isEqualTo("JA");
        assertThat(translation.text()).isEqualTo("こんにちは");
    }
}
