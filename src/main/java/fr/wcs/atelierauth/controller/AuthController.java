package fr.wcs.atelierauth.controller;

import fr.wcs.atelierauth.dto.JwtResponseDto;
import fr.wcs.atelierauth.dto.UserLoginDto;
import fr.wcs.atelierauth.service.AuthService;
import fr.wcs.atelierauth.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
// on autorise les CORS de toutes provenances
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody @Valid UserDto userDto){ authService.register(userDto); }

    @PostMapping("/login")
    public JwtResponseDto login(@RequestBody @Valid UserLoginDto userLoginDto) {
        return authService.login(userLoginDto);
    }
}
