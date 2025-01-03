package com.project.shopapp.controllers;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.dtos.responses.user.LoginResponse;
import com.project.shopapp.dtos.responses.user.RegisterResponse;
import com.project.shopapp.dtos.responses.user.UserListResponse;
import com.project.shopapp.dtos.responses.user.UserResponse;
import com.project.shopapp.services.token.ITokenService;
import com.project.shopapp.services.user.IUserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.shopapp.dtos.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserController.class);
  private final IUserService userService;
  private final LocalizationUtils localizationUtils;
  private final ITokenService tokenService;


  @GetMapping("")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> getAllUser(
      @RequestParam(defaultValue = "", required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int limit
  ) {
    try {

      PageRequest pageRequest = PageRequest.of(
          page, limit,
          Sort.by("id").ascending()
      );
      Page<UserResponse> userPage = userService.findAll(keyword, pageRequest)
          .map(UserResponse::fromUser);

      // Lấy tổng số trang
      int totalPages = userPage.getTotalPages();
      List<UserResponse> userResponses = userPage.getContent();
      return ResponseEntity.ok(UserListResponse
          .builder()
          .users(userResponses)
          .totalPages(totalPages)
          .build());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> registerUser(@RequestBody UserDTO userDTO) {
    RegisterResponse registerResponse = new RegisterResponse();

    try {
      userService.createUser(userDTO);
      registerResponse.setMessage("Registration successful");
      return ResponseEntity.ok(registerResponse);
    } catch (DataIntegrityViolationException e) {
      // Handle duplicate username or phone number
      registerResponse.setMessage("Error: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerResponse);
    } catch (DataNotFoundException e) {
      // Handle role does not exist
      registerResponse.setMessage("Error: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(registerResponse);
    } catch (PermissionDenyException e) {
      // Handle unauthorized role assignment
      registerResponse.setMessage("Error: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(registerResponse);
    } catch (Exception e) {
      // Handle all other exceptions
      registerResponse.setMessage("An unexpected error occurred: " + e.getMessage());
      // Log the exception for debugging
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(registerResponse);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
          @Valid @RequestBody UserLoginDTO userLoginDTO,
          HttpServletRequest request
  ) {
    try {
      log.debug("Attempting login for username: {}", userLoginDTO.getUserName());

      String token = userService.login(
              userLoginDTO.getUserName(),
              userLoginDTO.getPassword(),
              userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
      );

      if (token == null) {
        throw new RuntimeException("Token generation failed");
      }

      String userAgent = request.getHeader("User-Agent");
      User userDetail = userService.getUserDetailsFromToken(token);

      if (userDetail == null) {
        throw new RuntimeException("User details not found");
      }

      Token jwtToken = tokenService.addToken(userDetail, token);

      return ResponseEntity.ok(LoginResponse.builder()
              .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
              .token(jwtToken.getToken())
              .tokenType(jwtToken.getTokenType())
              .refreshToken(jwtToken.getRefreshToken())
              .username(userDetail.getUsername())
              .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
              .id(userDetail.getId())
              .build());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
              LoginResponse.builder()
                      .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                      .build()
      );
    }
  }



  @PostMapping("/refreshToken")
  public ResponseEntity<LoginResponse> refreshToken(
      @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
  ) {
    try {
      User userDetail =
          userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
      Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
      return ResponseEntity.ok(LoginResponse.builder()
          .message("Refresh token successfully")
          .token(jwtToken.getToken())
          .tokenType(jwtToken.getTokenType())
          .refreshToken(jwtToken.getRefreshToken())
          .username(userDetail.getUsername())
          .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
          .id(userDetail.getId())
          .build());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          LoginResponse.builder()
              .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED,
                  e.getMessage()))
              .build()
      );
    }
  }


  @PostMapping("/details")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
  public ResponseEntity<UserResponse> getUserDetails(
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    try {
      String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
      User user = userService.getUserDetailsFromToken(extractedToken);
      return ResponseEntity.ok(UserResponse.fromUser(user));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/details/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public ResponseEntity<UserResponse> updateUserDetails(
      @PathVariable Long userId,
      @RequestBody UpdateUserDTO updatedUserDTO,
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    try {
      String extractedToken = authorizationHeader.substring(7);
      User user = userService.getUserDetailsFromToken(extractedToken);
      // Ensure that the user making the request matches the user being updated
      if (user.getId() != userId) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      User updatedUser = userService.updateUser(userId, updatedUserDTO);
      return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/reset-password/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> resetPassword(@Valid @PathVariable long userId) {
    try {
      String newPassword = UUID.randomUUID().toString().substring(0, 5); // Tạo mật khẩu mới
      userService.resetPassword(userId, newPassword);
      return ResponseEntity.ok(newPassword);
    } catch (InvalidPasswordException e) {
      return ResponseEntity.badRequest().body("Invalid password");
    } catch (DataNotFoundException e) {
      return ResponseEntity.badRequest().body("User not found");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping("/block/{userId}/{active}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> blockOrEnable(
      @Valid @PathVariable long userId,
      @Valid @PathVariable int active
  ) {
    try {
      userService.blockOrEnable(userId, active > 0);
      String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the " +
          "user.";
      return ResponseEntity.ok().body(message);
    } catch (DataNotFoundException e) {
      return ResponseEntity.badRequest().body("User not found.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
