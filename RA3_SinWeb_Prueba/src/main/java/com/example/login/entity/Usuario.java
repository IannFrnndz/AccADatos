package com.example.login.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;



    @Column(nullable = false)
    private String rol = "USER";


    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private boolean bloqueado = false;

    @Column(nullable = false)
    private int intentosFallidos = 0;

    private LocalDateTime ultimoLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        hashearPassword();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void hashearPassword() {
        if (password != null && !password.startsWith("$2")) {
            password = new BCryptPasswordEncoder().encode(password);
        }
    }

    public boolean verificarPassword(String raw) {
        return new BCryptPasswordEncoder().matches(raw, password);
    }
}
