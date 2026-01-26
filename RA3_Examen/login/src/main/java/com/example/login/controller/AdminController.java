package com.example.login.controller;

import com.example.login.entity.Paciente;
import com.example.login. entity. Rol;
import com.example. login.entity.Usuario;
import com.example.login.service.PacienteService;
import com.example.login.service. RolService;
import com.example.login.service.UsuarioService;
import org.springframework. stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util. List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PacienteService pacienteService;


    public AdminController(UsuarioService usuarioService, RolService rolService, PacienteService pacienteService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.pacienteService = pacienteService;
    }

    // Listar todos los usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin";
    }
    // Listar todos los pacientes
    @GetMapping("/pacientes")
    public String listarPacientes(Model model) {
        List<Paciente> pacientes = pacienteService.listarPacientes();
        model.addAttribute("pacientes", pacientes);
        return "admin";
    }

    // Mostrar formulario para crear nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", true);
        return "user_form";
    }
    // Mostrar formulario para crear nuevo usuario
    @GetMapping("/pacientes/nuevo")
    public String mostrarFormularioNuevoPaciente(Model model) {
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", true);
        return "paciente_form";
    }
    // Guardar nuevo usuario
    @PostMapping("/usuarios")
    public String crearUsuario(@ModelAttribute Usuario usuario,
                               @RequestParam(required = false) String rolNombre,
                               RedirectAttributes redirectAttributes) {
        try {
            // Asignar rol
            if (rolNombre != null && ! rolNombre.isEmpty()) {
                Rol rol = rolService. buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles.add(rol);
                usuario.setRoles(roles);
            } else {
                // Por defecto, asignar rol USUARIO
                Rol rolUsuario = rolService.buscarPorNombre("USUARIO");
                Set<Rol> roles = new HashSet<>();
                roles.add(rolUsuario);
                usuario. setRoles(roles);
            }

            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Guardar nuevo paciente
    @PostMapping("/pacientes")
    public String crearPaciente(@ModelAttribute Paciente paciente,
                               @RequestParam(required = false) String rolNombre,
                               RedirectAttributes redirectAttributes) {
        try {
            // Asignar rol
            if (rolNombre != null && ! rolNombre.isEmpty()) {
                Rol rol = rolService. buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles.add(rol);
                paciente.setRoles(roles);
            } else {
                // Por defecto, asignar rol USUARIO
                Rol rolPaciente = rolService.buscarPorNombre("USUARIO");
                Set<Rol> roles = new HashSet<>();
                roles.add(rolPaciente);
                paciente. setRoles(roles);
            }

            pacienteService.crearPaciente(paciente);
            redirectAttributes.addFlashAttribute("mensaje", "Paciente creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        }
        return "redirect:/admin/pacientes";
    }

    // Mostrar formulario para editar usuario existente
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable String username, Model model) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", false);
        return "user_form";
    }

    // Mostrar formulario para editar paciente existente
    @GetMapping("/pacientes/editar/{id}")
    public String mostrarFormularioEditarPaciente(@PathVariable String username, Model model) {
        Paciente paciente = pacienteService.buscarPorUsername(username);
        model.addAttribute("paciente", paciente);
        model.addAttribute("roles", rolService.listarRoles());
        model.addAttribute("esNuevo", false);
        return "user_form";
    }

    // Actualizar usuario existente
    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @RequestParam String username,
                                    @RequestParam String nombre,
                                    @RequestParam(required = false) Boolean activo,
                                    @RequestParam(required = false) String rolNombre,
                                    RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            // Actualizar rol si se especificó
            if (rolNombre != null && !rolNombre. isEmpty()) {
                Rol rol = rolService.buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles. add(rol);
                usuario. setRoles(roles);
            }

            // Si activo es null, significa que el checkbox no fue marcado
            Boolean activoFinal = (activo != null) ? activo : false;

            usuarioService.actualizarUsuario(id, username,nombre, activoFinal);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Actualizar paciente existente
    @PostMapping("/pacientes/editar/{id}")
    public String actualizarPaciente(@PathVariable Long id,
                                    @RequestParam String username,
                                    @RequestParam String nombre,
                                    @RequestParam(required = false) Boolean activo,
                                    @RequestParam(required = false) String rolNombre,
                                    RedirectAttributes redirectAttributes) {
        try {
            Paciente paciente = pacienteService.buscarPorId(id);

            // Actualizar rol si se especificó
            if (rolNombre != null && !rolNombre. isEmpty()) {
                Rol rol = rolService.buscarPorNombre(rolNombre);
                Set<Rol> roles = new HashSet<>();
                roles. add(rol);
                paciente. setRoles(roles);
            }

            // Si activo es null, significa que el checkbox no fue marcado
            Boolean activoFinal = (activo != null) ? activo : false;

            usuarioService.actualizarUsuario(id, username,nombre, activoFinal);
            redirectAttributes.addFlashAttribute("mensaje", "Paciente actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar paciente: " + e.getMessage());
        }
        return "redirect:/admin/pacientes";
    }

    // Eliminar usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService. eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes. addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario
    @GetMapping("/pacientes/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService. eliminarPaciente(id);
            redirectAttributes.addFlashAttribute("mensaje", "Paciente eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes. addFlashAttribute("error", "Error al eliminar paciente: " + e.getMessage());
        }
        return "redirect:/admin/pacientes";
    }
}