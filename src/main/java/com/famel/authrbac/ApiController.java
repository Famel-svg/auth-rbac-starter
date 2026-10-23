package com.famel.authrbac;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private final UserRepository users;

    public ApiController(UserRepository users) { this.users = users; }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("email", auth.getName(), "roles", auth.getAuthorities().stream().map(Object::toString).toList());
    }

    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> usuarios() {
        return users.findAll().stream()
                .map(u -> Map.<String, Object>of("id", u.getId(), "email", u.getEmail(), "role", u.getRole()))
                .toList();
    }
}
