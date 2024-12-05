package com.example.beskbd.services;

import com.example.beskbd.dto.object.UserDTO;
import com.example.beskbd.dto.request.UserCreationRequest;
import com.example.beskbd.dto.response.AuthenticationResponse;
import com.example.beskbd.entities.Role;
import com.example.beskbd.entities.User;
import com.example.beskbd.exception.AppException;
import com.example.beskbd.exception.ErrorCode;
import com.example.beskbd.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService implements UserDetailsService {
    static Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment environment;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public AuthenticationResponse createUser(UserCreationRequest request) {
        // Check if the username is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .authority(Role.ROLE_USER)
                .isEnabled(true)// Initially, the email is not verified
                .build();

        userRepository.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token); // Store token in the user entity
        user.setResetTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Set expiry
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .authenticated(true)
                .build();
    }

    public void forgotPassword(String email, HttpServletRequest request) {
        logger.info("Requesting password reset for email: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTS));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);

        String resetUrl = "http://localhost:8083/reset-password?token=" + token;
        emailService.sendResetLink(email, "Password Reset Request",
                "Click the link (valid for 1 hour) to reset your password: " + resetUrl);
    }
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getAuthority().name()) // Assuming User has a method getAuthority() returning a role
                .build();
    }

    public void resendVerification(String email, HttpServletRequest request) {
        // Check if the user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found"); // Handle this appropriately in your API
        }

        // Generate a verification token (this could be a JWT or some other token)
        String token = generateVerificationToken(user);

        // Create the verification link
        String verificationUrl = request.getRequestURL().toString().replace(request.getServletPath(), "")
                + "/api/auth/verify?token=" + token;

        // Prepare the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.get().getEmail());
        message.setSubject("Verification Email");
        message.setText("Please click the following link to verify your email: " + verificationUrl);

        // Send the email
        mailSender.send(message);
    }
    private String generateVerificationToken(Optional<User> user) {
        // Define the expiration time (e.g., 24 hours)
        long expirationTime = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        // Generate a secret key for signing the JWT
        SecretKey key = Keys.hmacShaKeyFor(environment.getProperty("jwt.token.secret").getBytes(StandardCharsets.UTF_8));

        // Create the token

        return Jwts.builder()
                .subject(user.get().getEmail()) // Set the subject to the user's email
                .issuedAt(new Date()) // Set the issued date
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Set expiration date
                .signWith(key) // Sign the token with the key
                .compact();
    }

    public void deleteUserById(Long userId) {
        logger.info("Soft deleting user with ID: {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + userId));

        // Set the isDeleted flag to true
        user.setDeleted(true);
        userRepository.save(user); // Save the updated user back to the database
    }
}
