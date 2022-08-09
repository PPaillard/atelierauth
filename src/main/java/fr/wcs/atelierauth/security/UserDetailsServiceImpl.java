package fr.wcs.atelierauth.security;

import fr.wcs.atelierauth.entity.User;
import fr.wcs.atelierauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service qui permet à Spring de savoir comment il doit construire l'objet UserDetailsImpl
 * grace à un username
 * Il étends UserDetailsService pour que l'on soit obligé de remplir le contrat afin que Spring puisse savoir comment le construire.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    /**
     * Spring va se service de cette méthode pour récupèrer une entité par son username (ou son mail etc)
     * Il reçoit donc ça en paramètre et doit renvoyer un UserDetails
     * (ou UserDetailsImpl comme il implemente UserDetails)
     * @param username
     * @return Le UserDetails (ou UserDetailsImpl) compréhensible par Spring Security
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // aller recupérer l'entité User par son username
        User user = userRepository.findUserByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("L'utilisateur "+username+" n'a pas été trouvé")
        );
        // on doit construire un UserDetails et on le renvoit
        return new UserDetailsImpl(user);
    }
}
