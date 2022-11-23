package io.munkush.com.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends BaseResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.ok = false;
        this.message = message;
    }

}
