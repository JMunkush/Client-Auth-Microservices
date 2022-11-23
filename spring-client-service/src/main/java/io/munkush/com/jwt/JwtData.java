package io.munkush.com.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class JwtData {

    @Getter
    @Value("${jwt.cookie.name}")
    private String authCookieName;

    @Getter
    @Value("${jwt.cookie.refresh.name}")
    private String authCookieRefreshName;

}
