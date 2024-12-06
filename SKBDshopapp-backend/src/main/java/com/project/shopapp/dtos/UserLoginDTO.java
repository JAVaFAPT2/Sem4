package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
  @JsonProperty("user_name")
  @NotBlank(message = "userName is required")
  private String userName;

  @NotBlank(message = "Password cannot be blank")
  private String password;

  @Min(value = 1, message = "You must enter role's Id")
  @JsonProperty("role_id")
  private Long roleId;
}
