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
        // On va récuperer le role USER
        Role role = roleRepository.findRoleByAuthority("ROLE_USER").orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE)
        );

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    public JwtResponseDto login(UserLoginDto userLoginDto) {
        // verifier username / Password
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDto.getUsername(),userLoginDto.getPassword()));

        // récupérer le user en cours d'utilisation
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

        // on va générer le token JWT
        String token = utilsJWT.generateToken(userDetailsImpl);

        // on va renvoyer le token
        return new JwtResponseDto(userDetailsImpl.getUsername(), userDetailsImpl.getAuthorities(), token);
    }
}
