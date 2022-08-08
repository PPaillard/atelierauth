package fr.wcs.atelierauth.controller;

import fr.wcs.atelierauth.service.AuthService;
import fr.wcs.atelierauth.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody @Valid UserDto userDto){
        authService.register(userDto);
    }
}