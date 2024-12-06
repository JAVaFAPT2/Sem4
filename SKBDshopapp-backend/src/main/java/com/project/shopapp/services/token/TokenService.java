package com.project.shopapp.services.token;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.ExpiredTokenException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
  private static final int MAX_TOKENS = 3;  // Consider externalizing to properties
  private final TokenRepository tokenRepository;
  private final JwtTokenUtils jwtTokenUtil;

  @Value("${jwt.expiration}")
  private int expiration; // Save to an environment variable

  @Value("${jwt.expiration-refresh-token}")
  private int expirationRefreshToken;

  @Transactional
  @Override
  public Token refreshToken(String refreshToken, User user) throws Exception {
    if (user == null) {
      throw new DataNotFoundException("User not found");
    }

    Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
    if (existingToken == null) {
      throw new DataNotFoundException("Refresh token does not exist");
    }
    if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
      tokenRepository.delete(existingToken);
      throw new ExpiredTokenException("Refresh token is expired");
    }

    String token = jwtTokenUtil.generateToken(user);
    LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

    // Set updated values for the existing token
    existingToken.setExpirationDate(expirationDateTime);
    existingToken.setToken(token);
    existingToken.setRefreshToken(UUID.randomUUID().toString());
    existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

    // Save the updated token
    return existingToken;
  }

  @Transactional
  @Override
  public Token addToken(User user, String token, boolean isMobileDevice) {
    if (user == null || token == null || token.isEmpty()) {
      throw new IllegalArgumentException("User and token must not be null or empty");
    }

    List<Token> userTokens = tokenRepository.findByUser(user);
    int tokenCount = userTokens.size();

    if (tokenCount >= MAX_TOKENS) {
      Token tokenToDelete;
      boolean hasNonMobileToken = userTokens.stream().anyMatch(t -> !t.isMobile());

      if (hasNonMobileToken) {
        tokenToDelete = userTokens.stream()
                .filter(t -> !t.isMobile())
                .findFirst()
                .orElse(userTokens.get(0)); // Fallback to the first token if none found
      } else {
        tokenToDelete = userTokens.get(0); // All tokens are mobile, delete the first one
      }
      tokenRepository.delete(tokenToDelete);
    }

    LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
    Token newToken = Token.builder()
            .user(user)
            .token(token)
            .revoked(false)
            .expired(false)
            .tokenType("Bearer")
            .expirationDate(expirationDateTime)
            .isMobile(isMobileDevice)
            .refreshToken(UUID.randomUUID().toString())
            .refreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken))
            .build();

    return tokenRepository.save(newToken); // Save the new token
  }
}