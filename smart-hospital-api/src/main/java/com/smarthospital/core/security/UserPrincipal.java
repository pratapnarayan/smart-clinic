package com.smarthospital.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class UserPrincipal implements UserDetails {

    private final String userId;
    private final String tenantId;
    private final String email;
    private final List<String> roles;
    private final List<String> permissions;

    private UserPrincipal(Builder b) {
        this.userId      = b.userId;
        this.tenantId    = b.tenantId;
        this.email       = b.email;
        this.roles       = b.roles != null ? b.roles : List.of();
        this.permissions = b.permissions != null ? b.permissions : List.of();
    }

    public String       getUserId()     { return userId; }
    public String       getTenantId()   { return tenantId; }
    public String       getEmail()      { return email; }
    public List<String> getRoles()      { return roles; }
    public List<String> getPermissions(){ return permissions; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
                permissions.stream().map(SimpleGrantedAuthority::new)
        ).toList();
    }

    @Override public String  getPassword()             { return null; }   // never exposed
    @Override public String  getUsername()             { return email; }  // also used as email by Jackson
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String       userId;
        private String       tenantId;
        private String       email;
        private List<String> roles;
        private List<String> permissions;

        public Builder userId(String v)           { this.userId      = v; return this; }
        public Builder tenantId(String v)         { this.tenantId    = v; return this; }
        public Builder email(String v)            { this.email       = v; return this; }
        public Builder roles(List<String> v)      { this.roles       = v; return this; }
        public Builder permissions(List<String> v){ this.permissions = v; return this; }
        public UserPrincipal build()              { return new UserPrincipal(this); }
    }
}
