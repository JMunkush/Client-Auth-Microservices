package io.munkush.com.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PrincipalUser {
    private String email;
    private boolean enabled;
}