package com.example.beskbd.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    private String email;
}
