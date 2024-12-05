package com.example.beskbd.rest;

import com.example.beskbd.dto.response.AccountResponse;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.services.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 360000)
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class RestAccountController {
    public static Logger logger = LoggerFactory.getLogger(RestAccountController.class);
    AccountService accountService;

    @GetMapping()
    public ApiResponse<AccountResponse> getDetails() {
        logger.info("Account details request");
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.getAccountDetails())
                .success(true)
                .build();
    }

}
