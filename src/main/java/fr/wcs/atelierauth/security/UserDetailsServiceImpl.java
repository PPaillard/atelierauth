package fr.wcs.atelierauth.security;

import fr.wcs.atelierauth.entity.User;
import fr.wcs.atelierauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
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
