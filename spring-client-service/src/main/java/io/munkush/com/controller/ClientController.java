package io.munkush.com.controller;

import io.munkush.com.bean.PrincipalUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final PrincipalUserProvider principalUserProvider;

    @GetMapping(path = "/access")
    public ResponseEntity<String> getAccess() {

        return ResponseEntity.ok(principalUserProvider.get().isEnabled() ? "Access! :) " + principalUserProvider.get().getEmail() : "Forbidden! :(");
    }

}
