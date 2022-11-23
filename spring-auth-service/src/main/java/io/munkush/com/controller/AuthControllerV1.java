package io.munkush.com.controller;

import io.munkush.com.dto.*;
import io.munkush.com.entity.AppUserDetails;
import io.munkush.com.jwt.JwtTokenProvider;
import io.munkush.com.service.LoginService;
import io.munkush.com.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/** 23.11.2022 21:40
* author: Munkush)
*/

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;


    private void setAuthToken(AppUserDetails userDetails, HttpServletResponse httpServletResponse){
        String token = jwtTokenProvider.create(userDetails.getUsername(),userDetails.getRoles());
        Cookie cookie = new Cookie(jwtTokenProvider.getAuthCookieName(), token);
        cookie.setPath(jwtTokenProvider.getPath());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(jwtTokenProvider.getAuthExpiration());
        httpServletResponse.addCookie(cookie);
    }

    private void setRefreshToken(AppUserDetails userDetails, HttpServletResponse httpServletResponse){
        String token = jwtTokenProvider.create(userDetails.getUsername(),userDetails.getRoles());
        Cookie cookie = new Cookie(jwtTokenProvider.getAuthCookieRefreshName(), token);
        cookie.setPath(jwtTokenProvider.getPath());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(jwtTokenProvider.getAuthExpirationRefresh());
        httpServletResponse.addCookie(cookie);
    }

    @PostMapping(path = "/registration")
    public ResponseEntity<? extends BaseResponse> registration(@RequestBody RegistrationRequest registrationRequest){
            try {
                AppUserDetails appUserDetails = registrationService.register(registrationRequest);
                return userResponse(appUserDetails);
            } catch (Exception e){
                e.getLocalizedMessage();
                return errorResponse(String.format("Registration error %s", LocalDateTime
                        .now()
                        .format(DateTimeFormatter.ofPattern("HH:mm"))));
            }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<? extends BaseResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){
        try {
            AppUserDetails appUserDetails = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
            setAuthToken(appUserDetails, httpServletResponse);
            setRefreshToken(appUserDetails, httpServletResponse);
            return userResponse(appUserDetails);
        } catch (Exception e){
            e.getLocalizedMessage();
            return errorResponse(String.format("Login error %s", LocalDateTime
                    .now()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
    }
    @GetMapping(path = "/principal")
    public ResponseEntity<? extends BaseResponse> principal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if(authentication.isAuthenticated()) {
                return ResponseEntity.ok(new UserResponse(authentication.getName()));
            } else {
                throw new AuthenticationException();
            }

        } catch (NullPointerException | AuthenticationException e) {
            log.error(e.getLocalizedMessage());
            errorResponse("forbidden");
        }
        return userResponse(new AppUserDetails());
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<? extends BaseResponse> logout(HttpServletResponse httpServletResponse) {
        clearAuthAndRefreshTokens(httpServletResponse);
        SecurityContextHolder.clearContext();
        return userResponse(new AppUserDetails());
    }

    private void clearAuthAndRefreshTokens(HttpServletResponse httpServletResponse){
        Cookie authCookie = new Cookie(jwtTokenProvider.getAuthCookieName(), "null");
        authCookie.setPath(jwtTokenProvider.getPath());

        Cookie refreshCookie = new Cookie(jwtTokenProvider.getAuthCookieRefreshName(), "null");
        refreshCookie.setPath(jwtTokenProvider.getPath());

        httpServletResponse.addCookie(authCookie);
        httpServletResponse.addCookie(refreshCookie);
    }

    private ResponseEntity<UserResponse> userResponse(AppUserDetails appUserDetails){
        return ResponseEntity.ok(new UserResponse(appUserDetails));
    }
    private ResponseEntity<ErrorResponse> errorResponse(String message){
        return ResponseEntity.ok(new ErrorResponse(message));
    }

}
