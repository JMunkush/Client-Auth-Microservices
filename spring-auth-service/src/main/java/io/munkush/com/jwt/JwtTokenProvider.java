package io.munkush.com.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.munkush.com.entity.AppRole;
import io.munkush.com.entity.AppUserDetails;
import io.munkush.com.service.AppUserDetailsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final AppUserDetailsService userDetailsService;

    @PostConstruct
    public void init(){this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());}

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.cookie.name}")
    private String authCookieName;

    @Getter
    @Value("${jwt.cookie.refresh.name}")
    private String authCookieRefreshName;

    @Getter
    @Value("${jwt.token.expiration}")
    private Integer authExpiration;

    @Getter
    @Value("${jwt.token.expiration.refresh}")
    private Integer authExpirationRefresh;

    @Getter
    @Value("${jwt.token.path}")
    private String path;


    public String create(String username, List<AppRole> roles){
        List<String> stringRoleList = roles.stream().map(AppRole::getName)
                .collect(Collectors.toList());

        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + authExpiration);

        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        log.info("created token for {}", username);
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", stringRoleList)
                .withIssuedAt(issuedDate)
                .withExpiresAt(expirationDate)
                .sign(algorithm);

    }
    public Authentication getAuthentication(String token){
        DecodedJWT decodedJWT = getDecodedJWT(token);
        AppUserDetails userDetails = userDetailsService.findByUsername(decodedJWT.getSubject());
        log.info("user authentication: {}", decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    public boolean validateToken(String token){
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getExpiresAt().after(new Date());
    }

    private DecodedJWT getDecodedJWT(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();
        String result = null;
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(authCookieName))
                    result = cookie.getValue();
            }
        }
        return result;
    }

}
