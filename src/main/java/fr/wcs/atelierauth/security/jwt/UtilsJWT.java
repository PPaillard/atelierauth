package fr.wcs.atelierauth.security.jwt;

import fr.wcs.atelierauth.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class UtilsJWT {

    @Value("${wcslyon.app.jwtSecret}")
    private String jwtSecret;

    @Value("${wcslyon.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    /**
     * Méthode permettant de générer le token JWT
     *
     * @param userDetailsImpl : Contient l'utilisateur de Spring Security
     * @return le token JWT
     */
    public String generateToken(UserDetailsImpl userDetailsImpl) {
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
                .setExpiration(new Date(new Date().getTime() + this.jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
    }

    /**
     *
     * @param request la requete HTTP qui est en cours de traitement par le BACK
     * @return le token JWT si présent, sinon null
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        // On récupère le token dans l'entête HTTP (s'il est disponible, sinon null)
        String authRequest = request.getHeader("Authorization");
        // une autorisation est présent, et quelle contient au moins le format "Bearer exex.exexexe.exex"
        if(StringUtils.hasText(authRequest) && authRequest.length() > 7) {
            // on découpe le token et on le renvoi
            return authRequest.substring(7);
        }
        return null;
    }

    /**
     * Fonction permettant de faire valider le token JWT par la dependance JWTS
     * @param authToken le token JWT
     * @return true if token is valid, false if not
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // on tente d'extraire les claims (les informations du corps du token)
            // grace au parser et à la clé secrète qui a servi à (normalement) créer nos token
            // si ça se passe bien, on renvoi true (le but n'est pour l'instant pas de recuperer qqchose
            // mais de voir si simplement on peut y acceder.
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        // Si on ne peut pas extraire, c'est qu'une de ces exception va être soulevée
        catch (SignatureException e) {
            System.out.println("Signature JWT invalide");
        } catch (MalformedJwtException e) {
            System.out.println("Le token JWT est invalide");
        } catch (ExpiredJwtException e) {
            System.out.println("Le token JWT est expiré");
        } catch (UnsupportedJwtException e) {
            System.out.println("Le token JWT n'est pas supporté");
        } catch (IllegalArgumentException e) {
            System.out.println("Les claims du JWT sont vides");
        }
        
        return false;
    }

    /**
     * fonction permettant de recupèrer le username identifiant l'utilisateur, contenu dans les claims
     * @param token le token JWT
     * @return le username contenu dans le token JWT
     */
    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
