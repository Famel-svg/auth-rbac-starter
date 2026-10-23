package com.famel.authrbac;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Cria um ADMIN no startup se ADMIN_EMAIL e ADMIN_PASSWORD estiverem definidos. */
@Component
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;

    public AdminSeeder(UserRepository users, PasswordEncoder encoder,
                       @Value("${app.admin.email:}") String email, @Value("${app.admin.password:}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (!email.isBlank() && !password.isBlank() && users.findByEmail(email).isEmpty()) {
            users.save(new AppUser(email, encoder.encode(password), AppUser.Role.ADMIN));
        }
    }
}
