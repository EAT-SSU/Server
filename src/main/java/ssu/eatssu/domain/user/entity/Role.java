package ssu.eatssu.domain.user.entity;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

@Getter
public enum Role implements GrantedAuthority {
	USER("ROLE_USER", "유저"),
	ADMIN("ROLE_ADMIN", "관리자");

	private final String authority;
	private final String description;

	Role(String authority, String description) {
		this.authority = authority;
		this.description = description;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

}
