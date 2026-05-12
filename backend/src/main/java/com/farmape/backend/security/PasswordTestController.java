package com.farmape.backend.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class PasswordTestController {

    private final PasswordEncoder passwordEncoder;

    public PasswordTestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/bcrypt")
    public String generar(@RequestParam String clave) {
        return passwordEncoder.encode(clave);
    }
}