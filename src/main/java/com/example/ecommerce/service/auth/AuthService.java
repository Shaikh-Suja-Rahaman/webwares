package com.example.ecommerce.service.auth;

import com.example.ecommerce.dto.request.auth.LoginRequest;
import com.example.ecommerce.dto.request.auth.RegisterRequest;
import com.example.ecommerce.dto.response.auth.AuthResponse;
import com.example.ecommerce.exception.custom.BadRequestException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.Role;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.app.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final EmailService emailService;

        public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        EmailService emailService) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.emailService = emailService;
        }

        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BadRequestException("Email already registered");
                }
                User user = new User();
                user.setName(request.getName());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRoles(Set.of(Role.ROLE_USER));
                userRepository.save(user);
                UserDetails principal = new org.springframework.security.core.userdetails.User(
                                user.getEmail(), user.getPassword(),
                                user.getRoles().stream()
                                                .map(r -> (org.springframework.security.core.GrantedAuthority) () -> r
                                                                .name())
                                                .toList());
                String token = jwtService.generateToken(principal);
                try {
                        emailService.send(user.getEmail(), "Welcome to Webwares",
                                        "Hi " + user.getName() + ", thanks for registering.");
                } catch (Exception e) {
                        System.err.println("Failed to send welcome email: " + e.getMessage());
                }
                return new AuthResponse(token,
                                user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                                                request.getPassword()));
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
                UserDetails principal = new org.springframework.security.core.userdetails.User(
                                user.getEmail(), user.getPassword(),
                                user.getRoles().stream()
                                                .map(r -> (org.springframework.security.core.GrantedAuthority) () -> r
                                                                .name())
                                                .toList());
                String token = jwtService.generateToken(principal);
                return new AuthResponse(token,
                                user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
        }
}
