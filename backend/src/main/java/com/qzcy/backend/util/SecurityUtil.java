package com.qzcy.backend.util;

import com.qzcy.backend.config.security.JwtUserPrincipal;
import com.qzcy.backend.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    private SecurityUtil() {
    }

    public static JwtUserPrincipal current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal)) {
            throw new BusinessException(401, "请先登录");
        }
        return principal;
    }
}
