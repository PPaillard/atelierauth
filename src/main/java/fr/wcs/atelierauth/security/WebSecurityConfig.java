package fr.wcs.atelierauth.security;

import fr.wcs.atelierauth.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    AuthTokenFilter authTokenFilter;

    /**
     * On desactive les CSRF, on ne les utilisera pas dans notre contexte avec JWT
     * @param http l'objet représentant les requêtes sur lesquelles positionner nos règles et conditions de sécurité
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

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
