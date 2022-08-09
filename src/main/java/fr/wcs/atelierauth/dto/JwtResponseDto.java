package fr.wcs.atelierauth.dto;

import fr.wcs.atelierauth.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class JwtResponseDto {

    private String username;

    private String token;

    private List<Role> roles;

    public JwtResponseDto(String username, Collection<? extends GrantedAuthority> authorities, String token) {
        this.username = username;
        this.roles = (List<Role>) authorities;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
