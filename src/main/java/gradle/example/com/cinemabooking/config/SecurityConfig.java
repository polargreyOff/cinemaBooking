package gradle.example.com.cinemabooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CookieCsrfTokenRepository csrfTokenRepository
    )  {

        http
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/movies/**",
                                "/api/halls/**",
                                "/api/seats/**",
                                "/api/auth/logout"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf
                        // Login and registration must be callable before the SPA has
                        // received a CSRF cookie. Other state-changing endpoints stay
                        // protected by CSRF.
                        .ignoringRequestMatchers(
                                PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/api/auth/register"),
                                PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/api/auth/login")
                        )
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Content-Type",
                "X-XSRF-TOKEN"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}
