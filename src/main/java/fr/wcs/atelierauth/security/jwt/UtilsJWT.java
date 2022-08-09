package fr.wcs.atelierauth.security.jwt;

import fr.wcs.atelierauth.security.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UtilsJWT {

    @Value("${wcslyon.app.jwtSecret}")
    private String jwtSecret;

    @Value("${wcslyon.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateToken(UserDetailsImpl userDetailsImpl){

        return Jwts.builder()
                .setSubject(userDetailsImpl.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date( new Date().getTime() + this.jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
    }

}
