package com.project.shopapp.services.user;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;

import com.project.shopapp.exceptions.ExpiredTokenException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtils jwtTokenUtil;
  private final AuthenticationManager authenticationManager;
  private final LocalizationUtils localizationUtils;

  @Override
  @Transactional
  public User createUser(UserDTO userDTO) throws DataNotFoundException, PermissionDenyException {
    String username = userDTO.getUsername();
    if (userRepository.existsByUsername(username)) {
      throw new DataIntegrityViolationException("Username already exists");
    }

    Role role = roleRepository.findById(userDTO.getRoleId())
            .orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

    // Ensure that the role name is properly validated
    if (Role.ADMIN.equals(role.getName().toUpperCase())) {
      throw new PermissionDenyException("Role cannot be ADMIN");
    }
    User newUser = User.builder()
            .username(userDTO.getUsername())
            .fullName(userDTO.getFullName())
            .phoneNumber(userDTO.getPhoneNumber())
            .address(userDTO.getAddress())
            .dateOfBirth(userDTO.getDateOfBirth())
            .facebookAccountId(userDTO.getFacebookAccountId())
            .googleAccountId(userDTO.getGoogleAccountId())
            .active(true)
            .role(role)
            .build();

    // Set encoded password if accountId is not provided
    if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
      String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
      newUser.setPassword(encodedPassword);
    }
    try {
      return userRepository.save(newUser);
    } catch (DataIntegrityViolationException e) {
      // Log error details
      throw new DataIntegrityViolationException("Could not save user: " + e.getMessage());
    }
  }
  //!
  @Override
  public String login(
      String userName,
      String password,
      Long roleId
  ) throws Exception {
    Optional<User> optionalUser = userRepository.findByUsername(userName);
    if (optionalUser.isEmpty()) {
      throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
    }
    User existingUser = optionalUser.get();
    //check password
    if (existingUser.getFacebookAccountId() == 0
        && existingUser.getGoogleAccountId() == 0) {
      if (!passwordEncoder.matches(password, existingUser.getPassword())) {
        throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
      }
    }
    if (!optionalUser.get().isActive()) {
      throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
    }
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userName, password,
        existingUser.getAuthorities()
    );
    //authenticate with Java Spring security
    authenticationManager.authenticate(authenticationToken);
    return jwtTokenUtil.generateToken(existingUser);
  }
  //need fix
  @Transactional
  @Override
  public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws DataNotFoundException {
    // Find the existing user by userId
    User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));

    // Check if phone number provided is valid
    String newPhoneNumber = updatedUserDTO.getPhoneNumber();
    if (newPhoneNumber != null && !existingUser.getPhoneNumber().equals(newPhoneNumber) &&
            userRepository.existsByPhoneNumber(newPhoneNumber)) {
      throw new DataIntegrityViolationException("Phone number already exists");
    }

    // Update user information based on the DTO
    if (updatedUserDTO.getFullName() != null) {
      existingUser.setFullName(updatedUserDTO.getFullName());
    }
    if (newPhoneNumber != null) {
      existingUser.setPhoneNumber(newPhoneNumber);
    }
    // Continue updating other fields...

    return userRepository.save(existingUser);
  }

  @Override
  public User getUserDetailsFromToken(String token) throws Exception {
    if (jwtTokenUtil.isTokenExpired(token)) {
      throw new ExpiredTokenException("Token is expired");
    }
    String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
    Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

    if (user.isPresent()) {
      return user.get();
    } else {
      throw new Exception("User not found");
    }
  }

  @Override
  public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
    Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
    return getUserDetailsFromToken(existingToken.getToken());
  }

  @Override
  public Page<User> findAll(String keyword, Pageable pageable) {
    return userRepository.findAll(keyword, pageable);
  }

  @Override
  @Transactional
  public void resetPassword(Long userId, String newPassword)
      throws  DataNotFoundException {
    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException("User not found"));
    String encodedPassword = passwordEncoder.encode(newPassword);
    existingUser.setPassword(encodedPassword);
    userRepository.save(existingUser);
    //reset password => clear token
    List<Token> tokens = tokenRepository.findByUser(existingUser);
    for (Token token : tokens) {
      tokenRepository.delete(token);
    }
  }

  @Override
  @Transactional
  public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new DataNotFoundException("User not found"));
    existingUser.setActive(active);
    userRepository.save(existingUser);
  }
}
