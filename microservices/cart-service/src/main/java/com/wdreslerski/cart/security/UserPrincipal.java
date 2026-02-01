package com.wdreslerski.cart.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class UserPrincipal {
    private Long id;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
}
