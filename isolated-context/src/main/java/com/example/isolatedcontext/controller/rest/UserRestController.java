package com.example.isolatedcontext.controller.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "token")
public class UserRestController {

    @PostMapping("/api/v1/users")
    public List<Object> findAll() {
        return List.of();
    }

}
