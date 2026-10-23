package com.famel.authrbac;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8, max = 72) String password) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest r) {
        if (users.findByEmail(r.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email ja cadastrado");
        }
        AppUser u = users.save(new AppUser(r.email(), encoder.encode(r.password()), AppUser.Role.USER));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", u.getId(), "email", u.getEmail(), "role", u.getRole()));
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest r) {
        AppUser u = users.findByEmail(r.email())
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "credenciais invalidas"));
        return Map.of("accessToken", jwt.issue(u.getEmail(), u.getRole()), "tokenType", "Bearer");
    }
}
