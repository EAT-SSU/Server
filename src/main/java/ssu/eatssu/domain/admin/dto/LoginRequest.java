package ssu.eatssu.domain.admin.dto;

import ssu.eatssu.global.log.annotation.LogMask;

public record LoginRequest(String loginId,
                           @LogMask String password) {
}
