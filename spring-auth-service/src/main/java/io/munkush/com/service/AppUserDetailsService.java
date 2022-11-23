package io.munkush.com.service;

import io.munkush.com.entity.AppUserDetails;
import io.munkush.com.repo.AppUserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserDetails userDetails = userDetailsRepository.findByEmail(username);

        if(userDetails == null){
            log.error("user with email {} not found", username);
            throw new UsernameNotFoundException("user Not Found");
        }

        log.info("user with email {} has successfully fetched", username);

        return userDetails;
    }

    public AppUserDetails signUpUser(AppUserDetails appUserDetails) {
        AppUserDetails userDetails = userDetailsRepository.findByEmail(appUserDetails.getEmail());
        if(userDetails != null){
            log.error("user with email {} exists", userDetails.getEmail());
            throw new IllegalStateException("user exists");
        }
        appUserDetails.setPassword(passwordEncoder.encode(appUserDetails.getPassword()));
        appUserDetails.setEnabled(true);

        appUserDetails = userDetailsRepository.save(appUserDetails);
        if(!appUserDetails.isEnabled()){
            throw new IllegalStateException("the user is not enabled yet");
        }
        return appUserDetails;
    }

    public AppUserDetails findByUsername(String username){
        return userDetailsRepository.findByUsername(username);
    }
}
