package com.example.login.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util. Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    // -------------------------------
    // Callbacks JPA
    // -------------------------------
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        // Solo hashear si la contraseña no está ya hasheada
        if (this.password != null && !this. password.startsWith("$2a$")) {
            this.password = hashPassword(this.password);
        }
    }



    // -------------------------------
    // Métodos de seguridad con BCrypt
    // -------------------------------
    public String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.password);
    }




}