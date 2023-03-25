package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.dto.IdTokenRequestDto;
import com.devJeans.rabbit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.devJeans.rabbit.bind.ApiResult.failed;
import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com"},  allowCredentials = "true")
@RequestMapping("/v1/oauth")
public class LoginController {

    @Autowired
    AccountService accountService;

    @PostMapping("/login")
    public ApiResult LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        try {
            String authToken = accountService.loginOAuthGoogle(requestBody);
            final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                    .httpOnly(false)
                    .maxAge(7 * 24 * 3600)
                    .path("/")
                    .sameSite("None") // set SameSite attribute to "None"
                    .secure(true)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return succeed("JWT가 정상적으로 발급도었습니다.");
        } catch (IllegalArgumentException e) {
            return failed("Google id token이 만료되었습니다.");
        } catch (Exception e) {
            return failed(e.getMessage());
        }
    }

    @GetMapping("/health")
    public ApiResult healthCheck() {
        return succeed("health check success");
    }
}
