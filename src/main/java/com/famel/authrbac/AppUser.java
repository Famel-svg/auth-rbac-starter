package com.famel.authrbac;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {
    public enum Role { USER, ADMIN }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role = Role.USER;

    protected AppUser() {}

    public AppUser(String email, String passwordHash, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
}
