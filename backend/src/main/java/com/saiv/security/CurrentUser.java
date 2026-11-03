package com.saiv.security;

import com.saiv.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {}

    public static UsuarioPrincipal get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioPrincipal p)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return p;
    }

    public static Long id() {
        return get().id();
    }
}
