package io.munkush.com.service;

import io.munkush.com.dto.RegistrationRequest;
import io.munkush.com.entity.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AppUserDetailsService userDetailsService;

    public AppUserDetails register(RegistrationRequest registrationRequest){
        if(registrationRequest.getEmail().isEmpty())
            throw new BadCredentialsException("email is not corrected");

        return userDetailsService.signUpUser(new AppUserDetails(registrationRequest));
    }
}
