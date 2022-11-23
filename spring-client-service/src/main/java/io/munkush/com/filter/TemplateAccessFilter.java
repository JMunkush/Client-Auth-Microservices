package io.munkush.com.filter;

import com.google.gson.Gson;
import io.munkush.com.bean.PrincipalUserProvider;
import io.munkush.com.dto.PrincipalUser;
import io.munkush.com.jwt.JwtData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@Order(1)
@RequiredArgsConstructor
@Component
@Slf4j
public class TemplateAccessFilter implements Filter {
    private final JwtData jwtData;
    private final PrincipalUserProvider principalUserProvider;
    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        log.info("Client Filter: {}", httpServletRequest.getPathInfo());
        Cookie[] cookies = httpServletRequest.getCookies();
        log.info("Client Filter Cookies \n {}", Arrays.toString(cookies));
        PrincipalUser principalUser = new PrincipalUser();
        String token = null;

        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(jwtData.getAuthCookieName())){
                    token = cookie.getValue();
                }
            }
        }
        System.out.println(httpServletRequest.getHeader("Cookie"));

        if(token != null && !token.isEmpty()){
            try {

                URL url = new URL("http://localhost:8081/api/v1/auth/principal");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                connection.setRequestProperty("Cookie", httpServletRequest.getHeader("Cookie"));
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("GET");
                System.out.println("CODE: " + connection.getResponseCode());

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result;
                StringBuilder builder = new StringBuilder();

                while((result = br.readLine()) != null){
                    builder.append(result);
                }

                br.close();
                connection.disconnect();

                principalUser =  gson.fromJson(builder.toString(), PrincipalUser.class);


            } catch (IOException e){
                log.error("IOException");
            }
        } else {
            principalUser = new PrincipalUser();
        }
        log.info("Client principalUser {}", principalUser);

        principalUserProvider.set(principalUser);
        filterChain.doFilter(servletRequest, servletResponse);

    }
}
