package ssu.eatssu.domain.auth.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AppleKeysTest {

    @Test
    void findKeyByMatchesOnKidAndAlg() {
        AppleKeys.Key key = new AppleKeys.Key();
        ReflectionTestUtils.setField(key, "kty", "RSA");
        ReflectionTestUtils.setField(key, "kid", "test-kid");
        ReflectionTestUtils.setField(key, "use", "sig");
        ReflectionTestUtils.setField(key, "alg", "RS256");
        ReflectionTestUtils.setField(key, "n", "modulus");
        ReflectionTestUtils.setField(key, "e", "exponent");
        AppleKeys appleKeys = new AppleKeys();
        ReflectionTestUtils.setField(appleKeys, "keys", List.of(key));

        Optional<AppleKeys.Key> found = appleKeys.findKeyBy("test-kid", "RS256");

        assertThat(found).isPresent();
        assertThat(found.get().getKty()).isEqualTo("RSA");
        assertThat(found.get().getUse()).isEqualTo("sig");
        assertThat(found.get().getN()).isEqualTo("modulus");
        assertThat(found.get().getE()).isEqualTo("exponent");
        assertThat(appleKeys.getKeys()).containsExactly(key);
    }

    @Test
    void findKeyByReturnsEmptyWhenNoneMatch() {
        AppleKeys.Key key = new AppleKeys.Key();
        ReflectionTestUtils.setField(key, "kid", "other-kid");
        ReflectionTestUtils.setField(key, "alg", "RS256");
        AppleKeys appleKeys = new AppleKeys();
        ReflectionTestUtils.setField(appleKeys, "keys", List.of(key));

        assertThat(appleKeys.findKeyBy("test-kid", "RS256")).isEmpty();
    }
}
