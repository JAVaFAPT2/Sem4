package com.example.beskbd.services;

import com.example.beskbd.dto.request.AuthenticationRequest;
import com.example.beskbd.dto.request.LogoutRequest;
import com.example.beskbd.dto.request.RefreshRequest;
import com.example.beskbd.dto.response.AuthenticationResponse;
import com.example.beskbd.exception.AppException;
import com.example.beskbd.exception.ErrorCode;
import com.example.beskbd.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        user.getAuthorities().forEach(System.out::println);
        if (!isAuthenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = jwtService.generateToken(user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) {
        var checkValidToken = jwtService.isValidJwtToken(request.getRefreshToken());
        if (checkValidToken) {
            jwtService.invalidateToken(request.getRefreshToken());
            var username = jwtService.extractUserName(request.getRefreshToken());
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
            var token = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(token).authenticated(true).build();
        }
        throw new AppException(ErrorCode.UNSUPPORTED_TOKEN);
    }

    public void logout(LogoutRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var user = (UserDetails) auth.getPrincipal();
            String token = request.getToken();
            if (jwtService.validateToken(token, user)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            jwtService.invalidateToken(token);
            SecurityContextHolder.clearContext();
        }
    }


}
