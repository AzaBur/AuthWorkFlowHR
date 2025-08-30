package tranquillity.authworkflowhr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tranquillity.authworkflowhr.dto.auth.AuthenticationRequest;
import tranquillity.authworkflowhr.dto.auth.AuthenticationResponse;
import tranquillity.authworkflowhr.dto.auth.RegisterRequest;
import tranquillity.authworkflowhr.entity.User;
import tranquillity.authworkflowhr.enumiration.Role;
import tranquillity.authworkflowhr.exception.AuthenticationFailedException;
import tranquillity.authworkflowhr.exception.UserAlreadyExistsException;
import tranquillity.authworkflowhr.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles() != null ? request.getRoles() : Set.of(Role.ROLE_CANDIDATE))
                .enabled(true)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AuthenticationFailedException("User not found"));

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
    }
}