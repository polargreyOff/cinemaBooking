package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.api.AuthApi;
import gradle.example.com.cinemabooking.generated.model.LoginRequest;
import gradle.example.com.cinemabooking.generated.model.RegisterRequest;
import gradle.example.com.cinemabooking.generated.model.UserResponse;
import gradle.example.com.cinemabooking.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    private final CsrfLogoutHandler csrfLogoutHandler;

    @Autowired
    public  AuthController(AuthService authService,
                           AuthenticationManager authenticationManager,
                           SecurityContextRepository securityContextRepository,
                           CookieCsrfTokenRepository csrfTokenRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.csrfLogoutHandler = new CsrfLogoutHandler(csrfTokenRepository);
    }

    public ResponseEntity<Void> logout() {
        ServletRequestAttributes attributes =
        (ServletRequestAttributes)
        RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        logoutHandler.logout(
                request,
                response,
                authentication
        );
        csrfLogoutHandler.logout(
                request,
                response,
                authentication
        );

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResponse user = authService.getByLogin(authentication.getName());
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<UserResponse> register(RegisterRequest registerRequest) {
        UserResponse userResponse = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    public ResponseEntity<UserResponse> login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest servletRequest = servletRequestAttributes.getRequest();
        HttpServletResponse servletResponse = servletRequestAttributes.getResponse();

        servletRequest.getSession(true);
        servletRequest.changeSessionId();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
            context,
            servletRequest,
            servletResponse
        );

        UserResponse user = authService.getByLogin( authentication.getName());

        return  ResponseEntity.ok(user);
    }
}
