package fr.wcs.atelierauth.security;

import fr.wcs.atelierauth.entity.Role;
import fr.wcs.atelierauth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Classe contenant un utilisateur compréhensible par Spring avec les méthodes dont il a besoin par defaut
 * qui sont contenus dans l'interface UserDetails que l'on implémente
 */
public class UserDetailsImpl implements UserDetails {

    private String username;

    private String password;

    private List<Role> authorities;

    /**
     * Un UserDetails se construit grace à une entité User, on le fait donc.
     * @param user entity
     */
    public UserDetailsImpl(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
