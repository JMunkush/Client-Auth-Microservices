package io.munkush.com.dto;

import io.munkush.com.entity.AppUserDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends BaseResponse{
    private String email;
    private boolean enabled;

    public UserResponse(AppUserDetails appUserDetails){
        this.email = appUserDetails.getEmail();
        this.enabled = true;
    }
    public UserResponse(String email){
        this.email = email;
        this.enabled = true;
    }
}
