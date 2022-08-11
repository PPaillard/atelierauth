package fr.wcs.atelierauth.controller;

import fr.wcs.atelierauth.entity.User;
import fr.wcs.atelierauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    // Seul les utilisateurs ayant un role ROLE_ADMIN sont autorisés
    @PreAuthorize("hasRole('USER')")
    public List<User> getAll(){
        return userService.getAll();
    }

    /*
    Le FRONT demande au BACK de recupérer le profil en cours sans lui préciser l'ID
    Le token contient déjà l'identifiant de l'user en cours
     */
    @GetMapping("/myprofil")
    public User getMyProfil(){
        return userService.getMyProfil();
    }
}
