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

    /**
     * Méthode permettant de générer le token JWT
     * @param userDetailsImpl : Contient l'utilisateur de Spring Security
     * @return le token JWT
     */
    public String generateToken(UserDetailsImpl userDetailsImpl){
        // on utilise le générateur de token de la dépendance jjwt
        // on demande à la dependance de débuter le build du token
        // il contient le username stocké dans le userDetailsImpl
        // la date de creation
        // la date d'expiration (date actuelle en milliseconde depuis le 1 jan 1970
        // + le délai d'expiration positionné dans le application.properties en MS
        // On signe le token avec un algorithme et la passphrase secrète de notre application
        // on compact tout cela pour en faire un token qu'on renvoi
        return Jwts.builder()
                .setSubject(userDetailsImpl.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date( new Date().getTime() + this.jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
    }

}
