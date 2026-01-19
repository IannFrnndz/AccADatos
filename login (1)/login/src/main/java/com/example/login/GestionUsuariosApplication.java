package com.example.login;


import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class GestionUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionUsuariosApplication.class, args);
    }

    /**
     * CommandLineRunner que se ejecuta al iniciar la aplicación.
     * Demuestra todas las operaciones CRUD.
     */
    @Bean
    CommandLineRunner demoCRUD(UsuarioService usuarioService) {
        return args -> {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("   DEMOSTRACIÓN CRUD - Sistema de Gestión de Usuarios");
            System.out.println("=".repeat(60) + "\n");

            // ============================================
            // 1. TEST DE CONEXIÓN
            // ============================================
            System.out.println("1. TEST DE CONEXIÓN A BASE DE DATOS");
            System.out.println("-".repeat(40));
            try {
                List<Usuario> usuarios = usuarioService.obtenerTodos();
                System.out.println("✅ Conexión exitosa!");
                System.out.println("   Usuarios en BBDD: " + usuarios.size());
            } catch (Exception e) {
                System.out.println("❌ Error de conexión: " + e.getMessage());
                return;
            }
            System.out.println();

            // ============================================
            // 2. CREATE - Crear nuevo usuario
            // ============================================
            System.out.println("2. CREATE - Crear nuevo usuario");
            System.out.println("-".repeat(40));
            Usuario nuevoUsuario = null;
            try {
                nuevoUsuario = usuarioService.crearUsuario(
                        "test_user",
                        "test@ejemplo.com",
                        "password123"
                );
                System.out.println("✅ Usuario creado:");
                System.out.println("   ID: " + nuevoUsuario.getId());
                System.out.println("   Username: " + nuevoUsuario.getUsername());
                System.out.println("   Email: " + nuevoUsuario.getEmail());
                System.out.println("   Hash (primeros 20 chars): " +
                        nuevoUsuario.getPasswordHash().substring(0, 20) + "...");
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ " + e.getMessage());
                // Si ya existe, lo buscamos
                nuevoUsuario = usuarioService.obtenerPorUsername("test_user").orElse(null);
            }
            System.out.println();

            // ============================================
            // 3. READ - Listar todos los usuarios
            // ============================================
            System.out.println("3. READ - Listar todos los usuarios");
            System.out.println("-".repeat(40));
            List<Usuario> todosUsuarios = usuarioService.obtenerTodos();
            System.out.printf("%-5s %-15s %-25s %-8s%n", "ID", "USERNAME", "EMAIL", "ACTIVO");
            System.out.println("-".repeat(55));
            for (Usuario u : todosUsuarios) {
                System.out.printf("%-5d %-15s %-25s %-8s%n",
                        u.getId(),
                        u.getUsername(),
                        u.getEmail().length() > 24 ? u.getEmail().substring(0, 22) + ".." : u.getEmail(),
                        u.getActivo() ? "Sí" : "No"
                );
            }
            System.out.println();

            // ============================================
            // 4. READ - Buscar por username
            // ============================================
            System.out.println("4. READ - Buscar usuario por username");
            System.out.println("-".repeat(40));
            Optional<Usuario> encontrado = usuarioService.obtenerPorUsername("admin");
            if (encontrado.isPresent()) {
                Usuario u = encontrado.get();
                System.out.println("✅ Usuario 'admin' encontrado:");
                System.out.println("   ID: " + u.getId());
                System.out.println("   Email: " + u.getEmail());
                System.out.println("   Creado: " + u.getFechaCreacion());
            } else {
                System.out.println("❌ Usuario 'admin' no encontrado");
            }
            System.out.println();

            // ============================================
            // 5. UPDATE - Actualizar email
            // ============================================
            System.out.println("5. UPDATE - Actualizar email de usuario");
            System.out.println("-".repeat(40));
            if (nuevoUsuario != null) {
                Usuario actualizado = usuarioService.actualizarUsuario(
                        nuevoUsuario.getId(),
                        "nuevo_email@ejemplo.com",
                        null  // No cambiar contraseña
                );
                System.out.println("✅ Email actualizado:");
                System.out.println("   Email anterior: test@ejemplo.com");
                System.out.println("   Email nuevo: " + actualizado.getEmail());
                System.out.println("   Fecha actualización: " + actualizado.getFechaActualizacion());
            }
            System.out.println();

            // ============================================
            // 6. VERIFICAR CREDENCIALES (Login)
            // ============================================
            System.out.println("6. VERIFICAR CREDENCIALES - Simulación de login");
            System.out.println("-".repeat(40));

            // Login correcto
            Optional<Usuario> loginOk = usuarioService.verificarCredenciales("test_user", "password123");
            System.out.println("Login (test_user/password123): " +
                    (loginOk.isPresent() ? "✅ ÉXITO" : "❌ FALLIDO"));

            // Login incorrecto
            Optional<Usuario> loginFail = usuarioService.verificarCredenciales("test_user", "wrongpass");
            System.out.println("Login (test_user/wrongpass): " +
                    (loginFail.isPresent() ? "✅ ÉXITO" : "❌ FALLIDO (esperado)"));
            System.out.println();

            // ============================================
            // 7. DELETE LÓGICO - Desactivar usuario
            // ============================================
            System.out.println("7. DELETE LÓGICO - Desactivar usuario");
            System.out.println("-".repeat(40));
            if (nuevoUsuario != null) {
                Usuario desactivado = usuarioService.desactivarUsuario(nuevoUsuario.getId());
                System.out.println("✅ Usuario desactivado:");
                System.out.println("   Username: " + desactivado.getUsername());
                System.out.println("   Activo: " + desactivado.getActivo());

                // Intentar login con usuario desactivado
                Optional<Usuario> loginDesactivado =
                        usuarioService.verificarCredenciales("test_user", "password123");
                System.out.println("   Login tras desactivar: " +
                        (loginDesactivado.isPresent() ? "✅ ÉXITO" : "❌ BLOQUEADO (esperado)"));
            }
            System.out.println();

            // ============================================
            // 8. DELETE FÍSICO - Eliminar usuario
            // ============================================
            System.out.println("8. DELETE FÍSICO - Eliminar usuario permanentemente");
            System.out.println("-".repeat(40));
            if (nuevoUsuario != null) {
                Long idEliminar = nuevoUsuario.getId();
                usuarioService.eliminarUsuario(idEliminar);
                System.out.println("✅ Usuario eliminado permanentemente");

                // Verificar eliminación
                Optional<Usuario> verificar = usuarioService.obtenerPorId(idEliminar);
                System.out.println("   Buscar ID " + idEliminar + ": " +
                        (verificar.isPresent() ? "Encontrado" : "No encontrado (correcto)"));
            }
            System.out.println();

            // ============================================
            // RESUMEN FINAL
            // ============================================
            System.out.println("=".repeat(60));
            System.out.println("   DEMOSTRACIÓN COMPLETADA EXITOSAMENTE");
            System.out.println("=".repeat(60));
            System.out.println("\nUsuarios finales en BBDD: " +
                    usuarioService.obtenerTodos().size());
        };
    }
}