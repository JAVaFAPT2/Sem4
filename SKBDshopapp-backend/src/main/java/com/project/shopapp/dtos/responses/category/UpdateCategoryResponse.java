package com.project.shopapp.dtos.responses.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryResponse {
  @JsonProperty("message")
  private String message;
}
