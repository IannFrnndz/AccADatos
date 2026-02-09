package com.centromedico.gestion_pacientes.config;

import com.centromedico.gestion_pacientes.entity.*;
import com.centromedico.gestion_pacientes.repository.CitaRepository;
import com.centromedico.gestion_pacientes.repository.ConsultaRepository;
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
    private final ConsultaRepository consultaRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("🚀 INICIANDO CARGA DE DATOS DE PRUEBA");
        System.out.println("==========================================");

        long cantidadUsuarios = usuarioRepository.count();
        System.out.println("📊 Usuarios existentes en BD: " + cantidadUsuarios);

        if (cantidadUsuarios == 0) {
            System.out.println("⚠️ No hay usuarios en la BD. Creando usuarios de prueba...");
            crearUsuariosDePrueba();
        } else {
            System.out.println("✅ Ya existen usuarios en la BD.");
            usuarioRepository.findAll().forEach(usuario -> {
                System.out.println("   - " + usuario.getUsername() + " (" + usuario.getRol() + ") - Activo: " + usuario.getActivo());
            });
        }

        // ========================================
        // NUEVO: Crear citas y consultas
        // ========================================
        long cantidadCitas = citaRepository.count();
        if (cantidadCitas == 0) {
            System.out.println("\n📅 Creando citas de prueba...");
            crearCitasDePrueba();
        } else {
            System.out.println("\n✅ Ya existen citas en la BD (" + cantidadCitas + ")");
        }

//        long cantidadConsultas = consultaRepository.count();
//        if (cantidadConsultas == 0) {
//            System.out.println("\n📋 Creando consultas de prueba...");
//            crearConsultasDePrueba();
//        } else {
//            System.out.println("\n✅ Ya existen consultas en la BD (" + cantidadConsultas + ")");
//        }

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

        // 4. EFERMEERO
        Usuario enfermero = new Usuario();
        enfermero.setUsername("enfermero");
        enfermero.setEmail("enfermero@centromedico.com");
        enfermero.setPasswordHash(passwordEncriptado);
        enfermero.setNombre("Erai Hernandez Rodriguez");
        enfermero.setRol(Rol.ENFERMERO);
        enfermero.setActivo(true);
        usuarioRepository.save(enfermero);
        System.out.println("✅ Usuario creado: enfermero (ENFERMERO)");

        System.out.println("\n📊 Total usuarios creados: 5");

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

    /**
     * Crea citas de prueba para los médicos y pacientes existentes
     */
    private void crearCitasDePrueba() {
        // Obtener médicos
        Usuario drGarcia = usuarioRepository.findByUsername("dr.garcia")
                .orElseThrow(() -> new RuntimeException("Dr. García no encontrado"));
        Usuario draLopez = usuarioRepository.findByUsername("dra.lopez")
                .orElseThrow(() -> new RuntimeException("Dra. López no encontrada"));

        // Obtener pacientes
        List<Paciente> pacientes = pacienteRepository.findAll();
        if (pacientes.size() < 4) {
            System.out.println("⚠️ No hay suficientes pacientes para crear citas");
            return;
        }

        Paciente p1 = pacientes.get(0); // Carlos Rodríguez
        Paciente p2 = pacientes.get(1); // Laura Fernández
        Paciente p3 = pacientes.get(2); // Miguel Santos
        Paciente p4 = pacientes.get(3); // Elena Moreno

        // ========================================
        // CITAS DEL DR. GARCÍA
        // ========================================

        // CITA 1: PENDIENTE (mañana)
        Cita cita1 = new Cita();
        cita1.setFechaHora(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0));
        cita1.setDuracionMinutos(30);
        cita1.setMotivo("Revisión rutinaria");
        cita1.setEstado(Estado.PENDIENTE);
        cita1.setNotas("Primera cita del paciente");
        cita1.setMedico(drGarcia);
        cita1.setPaciente(p1);
        citaRepository.save(cita1);
        System.out.println("✅ Cita PENDIENTE creada: " + p1.getNombreCompleto() + " con " + drGarcia.getNombre());

        // CITA 2: CONFIRMADA (mañana tarde)
        Cita cita2 = new Cita();
        cita2.setFechaHora(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0).withSecond(0));
        cita2.setDuracionMinutos(30);
        cita2.setMotivo("Seguimiento de tratamiento");
        cita2.setEstado(Estado.CONFIRMADA);
        cita2.setNotas("Paciente confirmó asistencia por teléfono");
        cita2.setMedico(drGarcia);
        cita2.setPaciente(p2);
        citaRepository.save(cita2);
        System.out.println("✅ Cita CONFIRMADA creada: " + p2.getNombreCompleto() + " con " + drGarcia.getNombre());

        // CITA 3: REALIZADA (hace 2 días)
        Cita cita3 = new Cita();
        cita3.setFechaHora(LocalDateTime.now().minusDays(2).withHour(11).withMinute(0).withSecond(0));
        cita3.setDuracionMinutos(30);
        cita3.setMotivo("Control de hipertensión");
        cita3.setEstado(Estado.REALIZADA);
        cita3.setNotas("Paciente acudió puntualmente");
        cita3.setMedico(drGarcia);
        cita3.setPaciente(p1);
        citaRepository.save(cita3);
        System.out.println("✅ Cita REALIZADA creada: " + p1.getNombreCompleto() + " con " + drGarcia.getNombre());

        // ========================================
        // CITAS DE LA DRA. LÓPEZ
        // ========================================

        // CITA 4: PENDIENTE (hoy)
        Cita cita4 = new Cita();
        cita4.setFechaHora(LocalDateTime.now().withHour(14).withMinute(0).withSecond(0));
        cita4.setDuracionMinutos(30);
        cita4.setMotivo("Control de diabetes");
        cita4.setEstado(Estado.PENDIENTE);
        cita4.setNotas("Recordar revisar últimos análisis");
        cita4.setMedico(draLopez);
        cita4.setPaciente(p3);
        citaRepository.save(cita4);
        System.out.println("✅ Cita PENDIENTE (HOY) creada: " + p3.getNombreCompleto() + " con " + draLopez.getNombre());

        // CITA 5: CONFIRMADA (pasado mañana)
        Cita cita5 = new Cita();
        cita5.setFechaHora(LocalDateTime.now().plusDays(2).withHour(9).withMinute(30).withSecond(0));
        cita5.setDuracionMinutos(30);
        cita5.setMotivo("Consulta por alergias");
        cita5.setEstado(Estado.CONFIRMADA);
        cita5.setNotas("Paciente solicitó cita urgente");
        cita5.setMedico(draLopez);
        cita5.setPaciente(p4);
        citaRepository.save(cita5);
        System.out.println("✅ Cita CONFIRMADA creada: " + p4.getNombreCompleto() + " con " + draLopez.getNombre());

        // CITA 6: REALIZADA (hace 1 semana)
        Cita cita6 = new Cita();
        cita6.setFechaHora(LocalDateTime.now().minusDays(7).withHour(15).withMinute(0).withSecond(0));
        cita6.setDuracionMinutos(30);
        cita6.setMotivo("Revisión de asma");
        cita6.setEstado(Estado.REALIZADA);
        cita6.setNotas("Paciente mostró mejoría notable");
        cita6.setMedico(draLopez);
        cita6.setPaciente(p4);
        citaRepository.save(cita6);
        System.out.println("✅ Cita REALIZADA creada: " + p4.getNombreCompleto() + " con " + draLopez.getNombre());

        System.out.println("\n📊 Total citas creadas: 6");
        System.out.println("   - 2 PENDIENTES");
        System.out.println("   - 2 CONFIRMADAS");
        System.out.println("   - 2 REALIZADAS");
    }

    /**
     * Crea consultas de prueba para las citas realizadas
     */
    private void crearConsultasDePrueba() {
        // Obtener las citas REALIZADAS
        List<Cita> citasRealizadas = citaRepository.findAll().stream()
                .filter(c -> c.getEstado() == Estado.REALIZADA)
                .toList();
        if (citasRealizadas.isEmpty()) {
            System.out.println("⚠️ No hay citas realizadas para crear consultas");
            return;
        }

        int consultasCreadas = 0;

        // ========================================
        // CONSULTA 1: Para la cita realizada del Dr. García
        // ========================================
        for (Cita cita : citasRealizadas) {
            if (cita.getMedico().getUsername().equals("dr.garcia") && consultasCreadas == 0) {
                Consulta consulta1 = new Consulta();
                consulta1.setCita(cita);
                consulta1.setPaciente(cita.getPaciente());
                consulta1.setMedico(cita.getMedico());
                consulta1.setFechaConsulta(cita.getFechaHora());
                consulta1.setMotivoConsulta("Control rutinario de hipertensión arterial");
                consulta1.setSintomas("Paciente refiere leves dolores de cabeza ocasionales. Presión arterial estable.");
                consulta1.setDiagnostico("Hipertensión arterial controlada con tratamiento actual. Evolución favorable.");
                consulta1.setTratamiento("Continuar con Enalapril 10mg cada 12 horas. Dieta baja en sodio. Ejercicio moderado 30 min diarios.");
                consulta1.setObservaciones("Paciente cumple correctamente con indicaciones. Buen estado general.");
                consulta1.setProximaRevision(LocalDate.now().plusMonths(3));
                consultaRepository.save(consulta1);
                System.out.println("✅ Consulta creada para: " + cita.getPaciente().getNombreCompleto());
                consultasCreadas++;
                break;
            }
        }

        // ========================================
        // CONSULTA 2: Para la cita realizada de la Dra. López
        // ========================================
        for (Cita cita : citasRealizadas) {
            if (cita.getMedico().getUsername().equals("dra.lopez") && consultasCreadas == 1) {
                Consulta consulta2 = new Consulta();
                consulta2.setCita(cita);
                consulta2.setPaciente(cita.getPaciente());
                consulta2.setMedico(cita.getMedico());
                consulta2.setFechaConsulta(cita.getFechaHora());
                consulta2.setMotivoConsulta("Seguimiento de asma bronquial");
                consulta2.setSintomas("Paciente refiere mejoría significativa. Sin crisis asmáticas en el último mes. Uso ocasional de inhalador.");
                consulta2.setDiagnostico("Asma bronquial leve bien controlada. Sin signos de exacerbación.");
                consulta2.setTratamiento("Salbutamol inhalador (solo en caso necesario). Fluticasona 250mcg/12h. Evitar alérgenos conocidos.");
                consulta2.setObservaciones("Excelente respuesta al tratamiento. Paciente muy colaboradora. Educada sobre signos de alarma.");
                consulta2.setProximaRevision(LocalDate.now().plusMonths(2));
                consultaRepository.save(consulta2);
                System.out.println("✅ Consulta creada para: " + cita.getPaciente().getNombreCompleto());
                consultasCreadas++;
                break;
            }
        }

        // ========================================
        // CONSULTA 3: Consulta sin cita asociada (consulta directa)
        // ========================================
        Usuario drGarcia = usuarioRepository.findByUsername("dr.garcia").orElseThrow();
        Paciente paciente = pacienteRepository.findByDni("23456789B").orElse(null);

        if (paciente != null) {
            Consulta consulta3 = new Consulta();
            consulta3.setCita(null); // Sin cita asociada
            consulta3.setPaciente(paciente);
            consulta3.setMedico(drGarcia);
            consulta3.setFechaConsulta(LocalDateTime.now().minusDays(10).withHour(12).withMinute(0));
            consulta3.setMotivoConsulta("Consulta urgente por gripe estacional");
            consulta3.setSintomas("Fiebre de 38.5°C, dolor de garganta, congestión nasal, malestar general desde hace 2 días.");
            consulta3.setDiagnostico("Síndrome gripal. Cuadro viral respiratorio alto sin complicaciones.");
            consulta3.setTratamiento("Paracetamol 1g cada 8h por 5 días. Ibuprofeno 400mg si dolor. Abundantes líquidos. Reposo relativo.");
            consulta3.setObservaciones("Consulta sin cita previa por urgencia. Paciente en buen estado general. Se dan indicaciones de alarma.");
            consulta3.setProximaRevision(null); // No requiere seguimiento
            consultaRepository.save(consulta3);
            System.out.println("✅ Consulta SIN CITA creada para: " + paciente.getNombreCompleto());
            consultasCreadas++;
        }

        System.out.println("\n📊 Total consultas creadas: " + consultasCreadas);
        System.out.println("   - " + (consultasCreadas - 1) + " vinculadas a citas");
        System.out.println("   - 1 sin cita (consulta directa)");
    }
}