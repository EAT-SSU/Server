package ssu.eatssu.domain.auth.dto;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleKeys {
	private List<Key> keys;

	public Optional<Key> findKeyBy(String kid, String alg) {
		return this.keys.stream()
						.filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
						.findFirst();
	}

	@Getter
	@NoArgsConstructor
	public static class Key {
		private String kty;
		private String kid;
		private String use;
		private String alg;
		private String n;
		private String e;

	}
}
