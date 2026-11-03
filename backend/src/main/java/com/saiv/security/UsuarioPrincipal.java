package com.saiv.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record UsuarioPrincipal(Long id, String username, String rol, List<String> permisos) {
    public List<GrantedAuthority> authorities() {
        var list = new java.util.ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_" + rol));
        permisos.forEach(p -> list.add(new SimpleGrantedAuthority("PERM_" + p)));
        return list;
    }
}
