package com.example.beskbd.rest;

import com.example.beskbd.dto.request.*;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.response.AuthenticationResponse;
import com.example.beskbd.services.AuthenticationService;
import com.example.beskbd.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600000)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestAuthController {

    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        if (result == null) {
            return ApiResponse.<AuthenticationResponse>builder()
                    .errorMessage("Invalid credentials")
                    .errorCode(401)
                    .success(false)
                    .build();
        }
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .success(true)
                .build();
    }

    @GetMapping("oauth2/login")
    public ApiResponse<String> oauth2Login() {
        return ApiResponse.<String>builder()
                .data("Please login using Google OAuth2.")
                .success(true)
                .build();
    }

    @GetMapping("oauth2/callback")
    public ApiResponse<String> oauth2Callback(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ApiResponse.<String>builder()
                    .errorMessage("Error processing OAuth2 callback: OIDC user is null.")
                    .errorCode(500)
                    .success(false)
                    .build();
        }

        String email = oidcUser.getEmail();
        if (email == null || email.isEmpty()) {
            return ApiResponse.<String>builder()
                    .errorMessage("Error processing OAuth2 callback: Email not found in OIDC user.")
                    .errorCode(400)
                    .success(false)
                    .build();
        }

        String jwt = generateJwt(email);
        return ApiResponse.<String>builder()
                .data("JWT: " + jwt)
                .success(true)
                .build();
    }

    private String generateJwt(String email) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3000000L)) // 50 minutes
                .signWith(key)
                .compact();
    }

    @PostMapping("registration")
    public ApiResponse<AuthenticationResponse> registerUser(@RequestBody @Valid UserCreationRequest request) {
        AuthenticationResponse result = userService.createUser(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .success(true)
                .build();
    }

    @PostMapping("refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) {
        AuthenticationResponse result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .success(true)
                .build();
    }

    @PostMapping("sign-out")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @PostMapping("forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest requestBody,
                                              HttpServletRequest request) {
        userService.forgotPassword(requestBody.getEmail(), request);
        return ApiResponse.<String>builder()
                .data("Password reset link sent successfully.")
                .success(true)
                .build();
    }
    @PostMapping("resend-verification")
    public ApiResponse<String> resendVerification(@RequestBody @Valid ResendVerificationRequest requestBody,
                                                  HttpServletRequest request) {
        userService.resendVerification(requestBody.getEmail(), request);
        return ApiResponse.<String>builder()
                .data("Verification email sent successfully.")
                .success(true)
                .build();
    }


}