package com.centromedico.gestion_pacientes.config;

import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.PacienteRepository;
import com.centromedico.gestion_pacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Inicializador de datos
 * Se ejecuta al arrancar la aplicación y crea usuarios de prueba si no existen
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("🚀 INICIANDO CARGA DE DATOS DE PRUEBA");
        System.out.println("==========================================");

        // Verificar si ya existen usuarios
        long cantidadUsuarios = usuarioRepository.count();
        System.out.println("📊 Usuarios existentes en BD: " + cantidadUsuarios);

        if (cantidadUsuarios == 0) {
            System.out.println("⚠️ No hay usuarios en la BD. Creando usuarios de prueba...");
            crearUsuariosDePrueba();
        } else {
            System.out.println("✅ Ya existen usuarios en la BD.");

            // Mostrar los usuarios existentes
            usuarioRepository.findAll().forEach(usuario -> {
                System.out.println("   - " + usuario.getUsername() + " (" + usuario.getRol() + ") - Activo: " + usuario.getActivo());
            });

            // Opción: Actualizar contraseñas si es necesario
            System.out.println("\n🔄 ¿Quieres recrear las contraseñas? (comenta/descomenta esta línea)");
            // actualizarContrasenas(); // ⬅️ DESCOMENTA ESTA LÍNEA SI QUIERES RECREAR LAS CONTRASEÑAS
        }

        System.out.println("==========================================");
        System.out.println("✅ CARGA DE DATOS COMPLETADA");
        System.out.println("==========================================\n");
    }

    /**
     * Crea usuarios de prueba en la base de datos
     */
    private void crearUsuariosDePrueba() {
        String passwordPorDefecto = "password";
        String passwordEncriptado = passwordEncoder.encode(passwordPorDefecto);

        System.out.println("🔐 Contraseña por defecto para todos: " + passwordPorDefecto);
        System.out.println("🔐 Hash BCrypt generado: " + passwordEncriptado.substring(0, 30) + "...");

        // 1. ADMIN
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setEmail("admin@centromedico.com");
        admin.setPasswordHash(passwordEncriptado);
        admin.setNombre("Administrador Principal");
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);
        System.out.println("✅ Usuario creado: admin (ADMIN)");

        // 2. MEDICO 1
        Usuario drGarcia = new Usuario();
        drGarcia.setUsername("dr.garcia");
        drGarcia.setEmail("garcia@centromedico.com");
        drGarcia.setPasswordHash(passwordEncriptado);
        drGarcia.setNombre("Dr. Juan García Pérez");
        drGarcia.setRol(Rol.MEDICO);
        drGarcia.setActivo(true);
        usuarioRepository.save(drGarcia);
        System.out.println("✅ Usuario creado: dr.garcia (MEDICO)");

        // 3. MEDICO 2
        Usuario draLopez = new Usuario();
        draLopez.setUsername("dra.lopez");
        draLopez.setEmail("lopez@centromedico.com");
        draLopez.setPasswordHash(passwordEncriptado);
        draLopez.setNombre("Dra. María López Sánchez");
        draLopez.setRol(Rol.MEDICO);
        draLopez.setActivo(true);
        usuarioRepository.save(draLopez);
        System.out.println("✅ Usuario creado: dra.lopez (MEDICO)");

        // 4. RECEPCION
        Usuario recepcion = new Usuario();
        recepcion.setUsername("recepcion");
        recepcion.setEmail("recepcion@centromedico.com");
        recepcion.setPasswordHash(passwordEncriptado);
        recepcion.setNombre("Ana Martínez Torres");
        recepcion.setRol(Rol.RECEPCION);
        recepcion.setActivo(true);
        usuarioRepository.save(recepcion);
        System.out.println("✅ Usuario creado: recepcion (RECEPCION)");

        System.out.println("\n📊 Total usuarios creados: 4");

        // Crear pacientes de prueba
        crearPacientesDePrueba(drGarcia, draLopez);
    }

    /**
     * Actualiza las contraseñas de usuarios existentes
     * Útil si las contraseñas en BD están mal
     */
    private void actualizarContrasenas() {
        String passwordPorDefecto = "password";
        String passwordEncriptado = passwordEncoder.encode(passwordPorDefecto);

        System.out.println("\n🔄 ACTUALIZANDO CONTRASEÑAS DE USUARIOS EXISTENTES");
        System.out.println("🔐 Nueva contraseña: " + passwordPorDefecto);
        System.out.println("🔐 Nuevo hash: " + passwordEncriptado.substring(0, 30) + "...\n");

        usuarioRepository.findAll().forEach(usuario -> {
            String oldHash = usuario.getPasswordHash().substring(0, 30);
            usuario.setPasswordHash(passwordEncriptado);
            usuarioRepository.save(usuario);
            System.out.println("✅ Contraseña actualizada para: " + usuario.getUsername());
            System.out.println("   - Hash anterior: " + oldHash + "...");
            System.out.println("   - Hash nuevo:    " + passwordEncriptado.substring(0, 30) + "...");
        });

        System.out.println("\n✅ Todas las contraseñas han sido actualizadas");
    }

    /**
     * Crea pacientes de prueba
     */
    private void crearPacientesDePrueba(Usuario drGarcia, Usuario draLopez) {
        long cantidadPacientes = pacienteRepository.count();

        if (cantidadPacientes > 0) {
            System.out.println("\n✅ Ya existen pacientes en la BD (" + cantidadPacientes + ")");
            return;
        }

        System.out.println("\n👥 CREANDO PACIENTES DE PRUEBA");

        // Pacientes del Dr. García
        Paciente p1 = new Paciente();
        p1.setNombre("Carlos");
        p1.setApellidos("Rodríguez Gómez");
        p1.setDni("12345678A");
        p1.setTelefono("600111222");
        p1.setFechaNacimiento(LocalDate.of(1980, 5, 15));
        p1.setHistorial("Historial: Hipertensión controlada. Visitas regulares cada 6 meses.");
        p1.setMedico(drGarcia);
        p1.setActivo(true);
        pacienteRepository.save(p1);
        System.out.println("✅ Paciente creado: Carlos Rodríguez (Dr. García)");

        Paciente p2 = new Paciente();
        p2.setNombre("Laura");
        p2.setApellidos("Fernández Ruiz");
        p2.setDni("23456789B");
        p2.setTelefono("600222333");
        p2.setFechaNacimiento(LocalDate.of(1992, 8, 22));
        p2.setHistorial("Historial: Sin antecedentes relevantes. Última consulta por gripe estacional.");
        p2.setMedico(drGarcia);
        p2.setActivo(true);
        pacienteRepository.save(p2);
        System.out.println("✅ Paciente creado: Laura Fernández (Dr. García)");

        // Pacientes de la Dra. López
        Paciente p3 = new Paciente();
        p3.setNombre("Miguel");
        p3.setApellidos("Santos Díaz");
        p3.setDni("34567890C");
        p3.setTelefono("600333444");
        p3.setFechaNacimiento(LocalDate.of(1975, 11, 30));
        p3.setHistorial("Historial: Diabetes tipo 2. Tratamiento con metformina. Control trimestral.");
        p3.setMedico(draLopez);
        p3.setActivo(true);
        pacienteRepository.save(p3);
        System.out.println("✅ Paciente creado: Miguel Santos (Dra. López)");

        Paciente p4 = new Paciente();
        p4.setNombre("Elena");
        p4.setApellidos("Moreno Castro");
        p4.setDni("45678901D");
        p4.setTelefono("600444555");
        p4.setFechaNacimiento(LocalDate.of(1988, 3, 10));
        p4.setHistorial("Historial: Alergia a la penicilina. Tratamiento preventivo para asma leve.");
        p4.setMedico(draLopez);
        p4.setActivo(true);
        pacienteRepository.save(p4);
        System.out.println("✅ Paciente creado: Elena Moreno (Dra. López)");

        System.out.println("\n📊 Total pacientes creados: 4");
    }
}