package com.example.final_projects.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserPrincipal implements UserDetails {
    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Set<GrantedAuthority> authorities;

public CustomUserPrincipal(Long id, String email, String passwordHash, Collection<String> roles){
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());
    }

    public Long getId() {return id;}
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}
    @Override public String getPassword() {return passwordHash;}
    @Override public String getUsername() {return email;}
    @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return true;}
}
