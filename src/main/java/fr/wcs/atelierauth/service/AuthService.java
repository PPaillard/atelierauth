package fr.wcs.atelierauth.service;

import fr.wcs.atelierauth.dto.JwtResponseDto;
import fr.wcs.atelierauth.dto.UserDto;
import fr.wcs.atelierauth.dto.UserLoginDto;
import fr.wcs.atelierauth.entity.Role;
import fr.wcs.atelierauth.entity.User;
import fr.wcs.atelierauth.repository.RoleRepository;
import fr.wcs.atelierauth.repository.UserRepository;
import fr.wcs.atelierauth.security.UserDetailsImpl;
import fr.wcs.atelierauth.security.jwt.UtilsJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UtilsJWT utilsJWT;

    public void register(UserDto userDto) {
        // On va récuperer le role USER qu'on attribuera à tout nouvel utilisateur par defaut.
        // s'il n'est pas trouvé, on envoit une erreur 503
        Role role = roleRepository.findRoleByAuthority("ROLE_USER").orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE)
        );
        // on créé l'utilisateur avec les informations envoyées par le FRONT
        // On utilise l'encodeur de mot de passe défini par defaut par le bean dans la
        // classe WebSecurityConfig.
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // On est dans une relation ManyToMany entre user & role.
        // un user doit pouvoir avoir plusieurs roles attribués, on lui donne donc un tableau de role.
        // on créé donc le tableau, et on y insère le role par defaut (USER)
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    public JwtResponseDto login(UserLoginDto userLoginDto) {
        // On utilise le gestionnaire d'authentification de Spring et sa méthode authenticate pour
        // demander à Spring d'essayer d'authentifier "automatiquement" l'utilisateur
        // grace au couple username/password qu'on lui donne.
        // Spring utilisera le UserDetailsServiceImpl, comme spécifié dans le WebSecurityConfig
        // Si l'auth se passe bien, on obtient un objet de type Authentication
        // Cette objet authentication contient le UserDetailsImpl qu'on a construit pour être compris par Spring.
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDto.getUsername(),userLoginDto.getPassword()));

        // On place l'objet authentication dans le context de security de l'application au cas ou on en
        // ai besoin plus tard
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // on extrait l'utilisateur (UserDetailsImpl) du contexte de sécurité
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // Grace aux informations contenu dans le UserDetailsImpl
        // On va générer le token JWT
        String token = utilsJWT.generateToken(userDetailsImpl);

        // on va renvoyer le token dans un objet ne servant "que" de transporteur
        return new JwtResponseDto(userDetailsImpl.getUsername(), userDetailsImpl.getAuthorities(), token);
    }
}
