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

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(nullable = false)
    private int intentosFallidos = 0; // por defecto 0

    @Column(nullable = false)
    private boolean bloqueado = false; // por defecto no bloqueado



    // Callbacks de JPA
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Hashear contraseña antes de guardar
        if (this.password != null && !this.password.startsWith("$2a$")) { // evita re-hashear
            this.password = new BCryptPasswordEncoder().encode(this.password);
        }
    }




    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        if (this.password != null && !this.password.startsWith("$2a$")) {
            this.password = new BCryptPasswordEncoder().encode(this.password);
        }
    }

    // Método para verificar contraseña
    public boolean verificarPassword(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.password);
    }
}
