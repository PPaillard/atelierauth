package fr.wcs.atelierauth.security;

import fr.wcs.atelierauth.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    AuthTokenFilter authTokenFilter;

    @Value("${wcslyon.app.corsAllowed}")
    private String[] allowedOrigins;

    /*
     Configuration des CORS globales de l'application
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // on défini les URLS autorisées au niveau des CORS de l'application
        // récupération de ce qui est dans l'application properties transformé en List<String>
        configuration.setAllowedOrigins(Arrays.stream(this.allowedOrigins).toList());
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // on indique sur quel path s'applique les cors ci dessus (tous)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Méthode permettant de paramètrer la securité de notre requête HTTP
     * @param http l'objet représentant les requêtes sur lesquelles positionner nos règles et conditions de sécurité
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //On desactive les CSRF, on ne les utilisera pas dans notre contexte avec JWT
        http.csrf().disable();
        // On indique à Spring qu'il ne doit pas declencher le filtre d'authentification sur la route
        // qui comment par /auth avec n'importe quoi derrière
        http.authorizeRequests().antMatchers("/auth/**").permitAll();
        // On indique à Spring qu'il ne doit pas utiliser les sessions (au risque de voir des informations conservées
        // d'une requête à l'autre et d'avoir des incohérences.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // et que toutes les autres requêtes nécessite une authentification
        // /!\ Attention, cela va bloquer les accès PUBLIC
        // A n'utiliser que sur les routes dont vous savez qu'elles nécessitent d'être auth !
        http.authorizeRequests().anyRequest().authenticated();
        // On demande à Spring de placer un filtre. On lui précise à quel moment il doit se declencher
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // On indique à Spring quel service il va devoir utiliser pour créer
        // le UserDetails grace au username qu'on va lui passer
        // On lui indique également grace à quel encodeur de mot de passe il va pouvoir effectuer sa comparaison
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(this.getPasswordEncoder());
    }

    /**
     * On met à disposition de l'application de gestionnaire d'authentification de Spring
     * pour lui faire comprendre qu'il ne devra pas le faire automatiquement, c'est nous qui le declencherons
     * @return Le manager d'authentification
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     *     On définie le hasheur de mot de passe à utiliser par defaut par l'application pour
     *     Il permet de specifier la technique pour le hasher et la force du hashage.
     * @return Une instance de l'encodeur
     */
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
