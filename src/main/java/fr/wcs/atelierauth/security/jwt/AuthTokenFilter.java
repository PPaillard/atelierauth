package fr.wcs.atelierauth.security.jwt;

import fr.wcs.atelierauth.security.UserDetailsImpl;
import fr.wcs.atelierauth.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    UtilsJWT utilsJWT;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Ici on cherche à recuperer le token depuis la requete HTTP
        String token = utilsJWT.getTokenFromRequest(request);
        // Si notre token est recupérer et que notre token est valide
        if(token != null && utilsJWT.validateJwtToken(token)){
            // on va recuperer le username contenu dans notre token
            String username = utilsJWT.getUsernameFromToken(token);
            // On va recuperer le UserDetails identifié par le username
            UserDetailsImpl userDetails =userDetailsService.loadUserByUsername(username);

            // On va placer notre UserDetails dans le contexte de securité pour que Spring puisse l'utiliser.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }
}
