package com.centromedico.gestion_pacientes.config;

import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.EstadoCita;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.CitaRepository;
import com.centromedico.gestion_pacientes.repository.PacienteRepository;
import com.centromedico.gestion_pacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final CitaRepository citaRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("INICIANDO CARGA DE DATOS DE PRUEBA");
        System.out.println("==========================================");

        // Verificar si ya existen usuarios
        long cantidadUsuarios = usuarioRepository.count();
        System.out.println("Usuarios existentes en BD: " + cantidadUsuarios);

        if (cantidadUsuarios == 0) {
            System.out.println("No hay usuarios en la BD. Creando usuarios de prueba...");
            crearUsuariosDePrueba();
        } else {
            System.out.println("Ya existen usuarios en la BD.");

            // Mostrar los usuarios existentes
            usuarioRepository.findAll().forEach(usuario -> {
                System.out.println("   - " + usuario.getUsername() + " (" + usuario.getRol() + ") - Activo: " + usuario.getActivo());
            });
        }

        // Crear citas de prueba
        crearCitasDePrueba();

        System.out.println("==========================================");
        System.out.println("CARGA DE DATOS COMPLETADA");
        System.out.println("==========================================\n");
    }

    /**
     * Crea usuarios de prueba en la base de datos
     */
    private void crearUsuariosDePrueba() {
        String passwordPorDefecto = "password";
        String passwordEncriptado = passwordEncoder.encode(passwordPorDefecto);

        System.out.println("Contraseña por defecto para todos: " + passwordPorDefecto);
        System.out.println("Hash BCrypt generado: " + passwordEncriptado.substring(0, 30) + "...");

        // 1. ADMIN
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setEmail("admin@centromedico.com");
        admin.setPasswordHash(passwordEncriptado);
        admin.setNombre("Administrador Principal");
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);
        System.out.println("Usuario creado: admin (ADMIN)");

        // 2. MEDICO 1
        Usuario drGarcia = new Usuario();
        drGarcia.setUsername("dr.garcia");
        drGarcia.setEmail("garcia@centromedico.com");
        drGarcia.setPasswordHash(passwordEncriptado);
        drGarcia.setNombre("Dr. Juan Garcia Perez");
        drGarcia.setRol(Rol.MEDICO);
        drGarcia.setActivo(true);
        usuarioRepository.save(drGarcia);
        System.out.println("Usuario creado: dr.garcia (MEDICO)");

        // 3. MEDICO 2
        Usuario draLopez = new Usuario();
        draLopez.setUsername("dra.lopez");
        draLopez.setEmail("lopez@centromedico.com");
        draLopez.setPasswordHash(passwordEncriptado);
        draLopez.setNombre("Dra. Maria Lopez Sanchez");
        draLopez.setRol(Rol.MEDICO);
        draLopez.setActivo(true);
        usuarioRepository.save(draLopez);
        System.out.println("Usuario creado: dra.lopez (MEDICO)");

        // 4. RECEPCION
        Usuario recepcion = new Usuario();
        recepcion.setUsername("recepcion");
        recepcion.setEmail("recepcion@centromedico.com");
        recepcion.setPasswordHash(passwordEncriptado);
        recepcion.setNombre("Ana Martinez Torres");
        recepcion.setRol(Rol.RECEPCION);
        recepcion.setActivo(true);
        usuarioRepository.save(recepcion);
        System.out.println("Usuario creado: recepcion (RECEPCION)");

        System.out.println("\nTotal usuarios creados: 4");

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

        System.out.println("\nACTUALIZANDO CONTRASEÑAS DE USUARIOS EXISTENTES");
        System.out.println("Nueva contraseña: " + passwordPorDefecto);
        System.out.println("Nuevo hash: " + passwordEncriptado.substring(0, 30) + "...\n");

        usuarioRepository.findAll().forEach(usuario -> {
            String oldHash = usuario.getPasswordHash().substring(0, 30);
            usuario.setPasswordHash(passwordEncriptado);
            usuarioRepository.save(usuario);
            System.out.println("Contraseña actualizada para: " + usuario.getUsername());
            System.out.println("   - Hash anterior: " + oldHash + "...");
            System.out.println("   - Hash nuevo:    " + passwordEncriptado.substring(0, 30) + "...");
        });

        System.out.println("\nTodas las contraseñas han sido actualizadas");
    }

    /**
     * Crea pacientes de prueba
     */
    private void crearPacientesDePrueba(Usuario drGarcia, Usuario draLopez) {
        long cantidadPacientes = pacienteRepository.count();

        if (cantidadPacientes > 0) {
            System.out.println("\nYa existen pacientes en la BD (" + cantidadPacientes + ")");
            return;
        }

        System.out.println("\nCREANDO PACIENTES DE PRUEBA");

        // Pacientes del Dr. García
        Paciente p1 = new Paciente();
        p1.setNombre("Carlos");
        p1.setApellidos("Rodriguez Gomez");
        p1.setDni("12345678A");
        p1.setTelefono("600111222");
        p1.setFechaNacimiento(LocalDate.of(1980, 5, 15));
        p1.setHistorial("Historial: Hipertension controlada. Visitas regulares cada 6 meses.");
        p1.setMedico(drGarcia);
        p1.setActivo(true);
        pacienteRepository.save(p1);
        System.out.println("Paciente creado: Carlos Rodriguez (Dr. Garcia)");

        Paciente p2 = new Paciente();
        p2.setNombre("Laura");
        p2.setApellidos("Fernandez Ruiz");
        p2.setDni("23456789B");
        p2.setTelefono("600222333");
        p2.setFechaNacimiento(LocalDate.of(1992, 8, 22));
        p2.setHistorial("Historial: Sin antecedentes relevantes. Ultima consulta por gripe estacional.");
        p2.setMedico(drGarcia);
        p2.setActivo(true);
        pacienteRepository.save(p2);
        System.out.println("Paciente creado: Laura Fernandez (Dr. Garcia)");

        // Pacientes de la Dra. López
        Paciente p3 = new Paciente();
        p3.setNombre("Miguel");
        p3.setApellidos("Santos Diaz");
        p3.setDni("34567890C");
        p3.setTelefono("600333444");
        p3.setFechaNacimiento(LocalDate.of(1975, 11, 30));
        p3.setHistorial("Historial: Diabetes tipo 2. Tratamiento con metformina. Control trimestral.");
        p3.setMedico(draLopez);
        p3.setActivo(true);
        pacienteRepository.save(p3);
        System.out.println("Paciente creado: Miguel Santos (Dra. Lopez)");

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
        System.out.println("Paciente creado: Elena Moreno (Dra. Lopez)");

        System.out.println("\nTotal pacientes creados: 4");
    }

    /**
     * Crea citas de prueba en la base de datos
     * Crea citas con diferentes estados para probar todas las funcionalidades
     */
    private void crearCitasDePrueba() {
        System.out.println("\n========================================");
        System.out.println("CREANDO CITAS DE PRUEBA");
        System.out.println("========================================");

        // Verificar si ya existen citas
        long cantidadCitas = citaRepository.count();
        if (cantidadCitas > 0) {
            System.out.println("Ya existen " + cantidadCitas + " citas en la BD.");
            return;
        }

        // Obtener usuarios y pacientes existentes
        Usuario drGarcia = usuarioRepository.findByUsername("dr.garcia").orElse(null);
        Usuario draLopez = usuarioRepository.findByUsername("dra.lopez").orElse(null);

        if (drGarcia == null || draLopez == null) {
            System.out.println("No se encontraron medicos. Crea usuarios primero.");
            return;
        }

        List<Paciente> pacientes = pacienteRepository.findAll();
        if (pacientes.isEmpty()) {
            System.out.println("No se encontraron pacientes. Crea pacientes primero.");
            return;
        }

        // Para facilitar, tomamos los primeros pacientes
        Paciente paciente1 = pacientes.size() > 0 ? pacientes.get(0) : null;
        Paciente paciente2 = pacientes.size() > 1 ? pacientes.get(1) : null;
        Paciente paciente3 = pacientes.size() > 2 ? pacientes.get(2) : null;
        Paciente paciente4 = pacientes.size() > 3 ? pacientes.get(3) : null;

        LocalDateTime ahora = LocalDateTime.now();

        // ========================================
        // CITAS DE HOY
        // ========================================

        // Cita 1: Dr. García - HOY 10:00 - CONFIRMADA
        if (paciente1 != null) {
            Cita cita1 = new Cita();
            cita1.setPaciente(paciente1);
            cita1.setMedico(drGarcia);
            cita1.setFechaHora(ahora.withHour(10).withMinute(0).withSecond(0).withNano(0));
            cita1.setDuracionMinutos(30);
            cita1.setMotivo("Consulta de revision general");
            cita1.setEstado(EstadoCita.CONFIRMADA);
            cita1.setObservaciones("Paciente con seguimiento mensual");
            citaRepository.save(cita1);
            System.out.println("Cita creada: " + paciente1.getNombreCompleto() + " - Dr. Garcia - HOY 10:00 - CONFIRMADA");
        }

        // Cita 2: Dr. García - HOY 11:00 - PENDIENTE
        if (paciente2 != null) {
            Cita cita2 = new Cita();
            cita2.setPaciente(paciente2);
            cita2.setMedico(drGarcia);
            cita2.setFechaHora(ahora.withHour(11).withMinute(0).withSecond(0).withNano(0));
            cita2.setDuracionMinutos(30);
            cita2.setMotivo("Control de presion arterial");
            cita2.setEstado(EstadoCita.PENDIENTE);
            citaRepository.save(cita2);
            System.out.println("Cita creada: " + paciente2.getNombreCompleto() + " - Dr. Garcia - HOY 11:00 - PENDIENTE");
        }

        // Cita 3: Dra. López - HOY 15:00 - CONFIRMADA
        if (paciente3 != null) {
            Cita cita3 = new Cita();
            cita3.setPaciente(paciente3);
            cita3.setMedico(draLopez);
            cita3.setFechaHora(ahora.withHour(15).withMinute(0).withSecond(0).withNano(0));
            cita3.setDuracionMinutos(60);
            cita3.setMotivo("Consulta especializada - Evaluacion completa");
            cita3.setEstado(EstadoCita.CONFIRMADA);
            citaRepository.save(cita3);
            System.out.println("Cita creada: " + paciente3.getNombreCompleto() + " - Dra. Lopez - HOY 15:00 - CONFIRMADA");
        }

        // ========================================
        // CITAS PASADAS (AYER)
        // ========================================

        // Cita 4: Dr. García - AYER 09:00 - COMPLETADA
        if (paciente1 != null) {
            Cita cita4 = new Cita();
            cita4.setPaciente(paciente1);
            cita4.setMedico(drGarcia);
            cita4.setFechaHora(ahora.minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
            cita4.setDuracionMinutos(30);
            cita4.setMotivo("Consulta de seguimiento");
            cita4.setEstado(EstadoCita.COMPLETADA);
            cita4.setObservaciones("Paciente evolucionando favorablemente. Continuar tratamiento.");
            citaRepository.save(cita4);
            System.out.println("Cita creada: " + paciente1.getNombreCompleto() + " - Dr. Garcia - AYER 09:00 - COMPLETADA");
        }

        // Cita 5: Dra. López - AYER 14:00 - COMPLETADA
        if (paciente2 != null) {
            Cita cita5 = new Cita();
            cita5.setPaciente(paciente2);
            cita5.setMedico(draLopez);
            cita5.setFechaHora(ahora.minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0));
            cita5.setDuracionMinutos(45);
            cita5.setMotivo("Revision de examenes de laboratorio");
            cita5.setEstado(EstadoCita.COMPLETADA);
            cita5.setObservaciones("Examenes normales. Alta medica.");
            citaRepository.save(cita5);
            System.out.println("Cita creada: " + paciente2.getNombreCompleto() + " - Dra. Lopez - AYER 14:00 - COMPLETADA");
        }

        // Cita 6: Dr. García - HACE 2 DÍAS - CANCELADA
        if (paciente3 != null) {
            Cita cita6 = new Cita();
            cita6.setPaciente(paciente3);
            cita6.setMedico(drGarcia);
            cita6.setFechaHora(ahora.minusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0));
            cita6.setDuracionMinutos(30);
            cita6.setMotivo("Consulta general");
            cita6.setEstado(EstadoCita.CANCELADA);
            cita6.setObservaciones("[CANCELACION] Paciente no pudo asistir por motivos personales");
            citaRepository.save(cita6);
            System.out.println("Cita creada: " + paciente3.getNombreCompleto() + " - Dr. Garcia - HACE 2 DIAS - CANCELADA");
        }

        // ========================================
        // CITAS FUTURAS
        // ========================================

        // Cita 7: Dr. García - MAÑANA 10:00 - CONFIRMADA
        if (paciente1 != null) {
            Cita cita7 = new Cita();
            cita7.setPaciente(paciente1);
            cita7.setMedico(drGarcia);
            cita7.setFechaHora(ahora.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
            cita7.setDuracionMinutos(30);
            cita7.setMotivo("Control post-tratamiento");
            cita7.setEstado(EstadoCita.CONFIRMADA);
            citaRepository.save(cita7);
            System.out.println("Cita creada: " + paciente1.getNombreCompleto() + " - Dr. Garcia - MANANA 10:00 - CONFIRMADA");
        }

        // Cita 8: Dra. López - MAÑANA 16:00 - PENDIENTE
        if (paciente4 != null) {
            Cita cita8 = new Cita();
            cita8.setPaciente(paciente4);
            cita8.setMedico(draLopez);
            cita8.setFechaHora(ahora.plusDays(1).withHour(16).withMinute(0).withSecond(0).withNano(0));
            cita8.setDuracionMinutos(30);
            cita8.setMotivo("Primera consulta");
            cita8.setEstado(EstadoCita.PENDIENTE);
            citaRepository.save(cita8);
            System.out.println("Cita creada: " + paciente4.getNombreCompleto() + " - Dra. Lopez - MANANA 16:00 - PENDIENTE");
        }

        // Cita 9: Dr. García - EN 3 DÍAS 09:00 - CONFIRMADA
        if (paciente2 != null) {
            Cita cita9 = new Cita();
            cita9.setPaciente(paciente2);
            cita9.setMedico(drGarcia);
            cita9.setFechaHora(ahora.plusDays(3).withHour(9).withMinute(0).withSecond(0).withNano(0));
            cita9.setDuracionMinutos(30);
            cita9.setMotivo("Seguimiento de tratamiento");
            cita9.setEstado(EstadoCita.CONFIRMADA);
            citaRepository.save(cita9);
            System.out.println("Cita creada: " + paciente2.getNombreCompleto() + " - Dr. Garcia - EN 3 DIAS 09:00 - CONFIRMADA");
        }

        // Cita 10: Dra. López - EN 5 DÍAS 11:00 - PENDIENTE
        if (paciente3 != null) {
            Cita cita10 = new Cita();
            cita10.setPaciente(paciente3);
            cita10.setMedico(draLopez);
            cita10.setFechaHora(ahora.plusDays(5).withHour(11).withMinute(0).withSecond(0).withNano(0));
            cita10.setDuracionMinutos(45);
            cita10.setMotivo("Revision de resultados");
            cita10.setEstado(EstadoCita.PENDIENTE);
            citaRepository.save(cita10);
            System.out.println("Cita creada: " + paciente3.getNombreCompleto() + " - Dra. Lopez - EN 5 DIAS 11:00 - PENDIENTE");
        }

        // Cita 11: Dr. García - EN 7 DÍAS 14:00 - PENDIENTE
        if (paciente1 != null) {
            Cita cita11 = new Cita();
            cita11.setPaciente(paciente1);
            cita11.setMedico(drGarcia);
            cita11.setFechaHora(ahora.plusDays(7).withHour(14).withMinute(0).withSecond(0).withNano(0));
            cita11.setDuracionMinutos(60);
            cita11.setMotivo("Evaluacion mensual completa");
            cita11.setEstado(EstadoCita.PENDIENTE);
            citaRepository.save(cita11);
            System.out.println("Cita creada: " + paciente1.getNombreCompleto() + " - Dr. Garcia - EN 7 DIAS 14:00 - PENDIENTE");
        }

        // Cita 12: Dra. López - EN 10 DÍAS 10:00 - PENDIENTE
        if (paciente4 != null) {
            Cita cita12 = new Cita();
            cita12.setPaciente(paciente4);
            cita12.setMedico(draLopez);
            cita12.setFechaHora(ahora.plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0));
            cita12.setDuracionMinutos(30);
            cita12.setMotivo("Control preventivo");
            cita12.setEstado(EstadoCita.PENDIENTE);
            citaRepository.save(cita12);
            System.out.println("Cita creada: " + paciente4.getNombreCompleto() + " - Dra. Lopez - EN 10 DIAS 10:00 - PENDIENTE");
        }

        long totalCitas = citaRepository.count();
        System.out.println("\nTotal de citas creadas: " + totalCitas);
        System.out.println("========================================\n");
    }
}