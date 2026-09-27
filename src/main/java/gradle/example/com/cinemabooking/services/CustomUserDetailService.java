package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.UserNotFound;
import gradle.example.com.cinemabooking.models.UserData;
import gradle.example.com.cinemabooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserData user = userRepository.findByLogin(username).orElseThrow(
                () -> new UserNotFound(username)
        );

        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword_hash())
                .roles("USER")
                .build();
    }
}
