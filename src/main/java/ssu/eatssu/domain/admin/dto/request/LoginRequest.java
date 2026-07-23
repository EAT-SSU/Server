package ssu.eatssu.domain.admin.dto.request;

import ssu.eatssu.global.log.annotation.LogMask;

public record LoginRequest(String loginId,
                           @LogMask String password) {
}
