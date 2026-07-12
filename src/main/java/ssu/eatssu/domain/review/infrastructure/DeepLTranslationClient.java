package ssu.eatssu.domain.review.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseException;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.TRANSLATION_FAILED;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.TRANSLATION_QUOTA_EXCEEDED;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.TRANSLATION_TIMEOUT;

@Component
@RequiredArgsConstructor
public class DeepLTranslationClient {

    private static final int QUOTA_EXCEEDED_STATUS = 456;

    private final RestTemplate deeplRestTemplate;

    @Value("${deepl.api-key}")
    private String apiKey;

    @Value("${deepl.base-url}")
    private String baseUrl;

    public String translate(String text, Language targetLanguage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "DeepL-Auth-Key " + apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("target_lang", targetLanguage.name());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            DeepLTranslateResponse response = deeplRestTemplate.postForObject(
                    baseUrl + "/v2/translate", request, DeepLTranslateResponse.class);

            if (response == null || response.translations().isEmpty()) {
                throw new BaseException(TRANSLATION_FAILED);
            }
            return response.translations().get(0).text();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            int statusCode = e.getStatusCode().value();
            if (statusCode == 429 || statusCode == QUOTA_EXCEEDED_STATUS) {
                throw new BaseException(TRANSLATION_QUOTA_EXCEEDED);
            }
            throw new BaseException(TRANSLATION_FAILED);
        } catch (ResourceAccessException e) {
            throw new BaseException(TRANSLATION_TIMEOUT);
        } catch (RestClientException e) {
            throw new BaseException(TRANSLATION_FAILED);
        }
    }
}
