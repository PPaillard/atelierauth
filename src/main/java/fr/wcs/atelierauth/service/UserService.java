package fr.wcs.atelierauth.service;

import fr.wcs.atelierauth.entity.User;
import fr.wcs.atelierauth.repository.UserRepository;
import fr.wcs.atelierauth.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getMyProfil() {
        // Dans le filtre, on a pris l'utilisateur concerné par le token, et on l'a mis dans le contexte de securité.
        // on peut allé le recupérer pour renvoyer l'utilisateur à l'appellant
        UserDetailsImpl userDetails= (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // grace au username contenu dans le userDetails de Spring, on peut récuperer l'objet user de la personne
        // actuellement connectée et le renvoyer.
        return userRepository.findUserByUsername(userDetails.getUsername()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }
}
