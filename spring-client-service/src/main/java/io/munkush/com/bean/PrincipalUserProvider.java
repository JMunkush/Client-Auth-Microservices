package io.munkush.com.bean;

import io.munkush.com.dto.PrincipalUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class PrincipalUserProvider {
    private final HttpServletRequest httpServletRequest;

    public void set(PrincipalUser principalUser){
        httpServletRequest.setAttribute("PrincipalUser", principalUser);
    }

    public PrincipalUser get(){
        PrincipalUser currentUser = (PrincipalUser) httpServletRequest.getAttribute("PrincipalUser");
        if(currentUser == null){
            return new PrincipalUser();
        }
        return currentUser;
    }
}
