package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.LoginAlreadyExistsException;
import gradle.example.com.cinemabooking.exceptions.UserNotFound;
import gradle.example.com.cinemabooking.generated.model.RegisterRequest;
import gradle.example.com.cinemabooking.generated.model.UserResponse;
import gradle.example.com.cinemabooking.mappers.UserMapper;
import gradle.example.com.cinemabooking.models.Role;
import gradle.example.com.cinemabooking.models.UserData;
import gradle.example.com.cinemabooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse register(RegisterRequest registerRequest) {

        String password_hash = passwordEncoder.encode(registerRequest.getPassword());

        UserData user = new UserData(
                null,
                registerRequest.getName(),
                registerRequest.getLogin(),
                registerRequest.getPhone(),
                password_hash,
                Role.USER
        );

        try {
            UserData savedUser = userRepository.save(user);
            UserResponse response = new UserResponse();

            response.setId(savedUser.getId());
            response.setName(savedUser.getName());
            response.setLogin(savedUser.getLogin());
            response.setPhone(savedUser.getPhone());

            return response;
        } catch(DuplicateKeyException e) {
            throw new LoginAlreadyExistsException();
        }
    }

    public UserResponse getByLogin(String login) {
        UserData user = userRepository.findByLogin(login).orElseThrow(
                () -> new UserNotFound(login)
        );
        UserResponse response  = userMapper.toUserResponse(user);
        return response;
    }
}
