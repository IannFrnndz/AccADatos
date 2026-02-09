package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de usuarios
 * Solo accesible para ADMIN
 */
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PacienteService pacienteService;

    // ============================================
    // LISTAR USUARIOS
    // ============================================

    /**
     * Lista todos los usuarios del sistema
     * Solo ADMIN
     */
    @GetMapping({"", "/", "/lista"})
    public String listarUsuarios(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();

        // Calcular estadísticas por rol
        long totalAdmins = usuarios.stream()
                .filter(u -> u.getRol() == Rol.ADMIN)
                .count();

        long totalMedicos = usuarios.stream()
                .filter(u -> u.getRol() == Rol.MEDICO)
                .count();

        long totalRecepcion = usuarios.stream()
                .filter(u -> u.getRol() == Rol.RECEPCION)
                .count();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalMedicos", totalMedicos);
        model.addAttribute("totalRecepcion", totalRecepcion);

        return "usuarios/lista";
    }

    // ============================================
    // VER DETALLE DE USUARIO
    // ============================================

    /**
     * Muestra el detalle de un usuario específico
     */
    @GetMapping("/ver/{id}")
    public String verUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Si es médico, obtener sus pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            long cantidadPacientes = pacienteService.contarPacientesPorMedico(usuario.getId());
            model.addAttribute("cantidadPacientes", cantidadPacientes);
        }

        model.addAttribute("usuarioDetalle", usuario);
        model.addAttribute("usuario", userDetails.getUsuario());

        return "usuarios/detalle";
    }

    // ============================================
    // CREAR USUARIO
    // ============================================

    /**
     * Muestra el formulario para crear un nuevo usuario
     */
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);

        model.addAttribute("usuarioForm", usuario);
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("roles", Rol.values());

        return "usuarios/formulario";
    }

    /**
     * Procesa la creación de un nuevo usuario
     */
    @PostMapping("/crear")
    public String crearUsuario(
            @ModelAttribute("usuarioForm") Usuario usuario,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Establecer la contraseña (se encriptará automáticamente en el servicio)
            usuario.setPasswordHash(password);

            Usuario usuarioGuardado = usuarioService.crearUsuario(usuario);

            redirectAttributes.addFlashAttribute("success",
                    "Usuario creado correctamente: " + usuarioGuardado.getUsername());

            return "redirect:/usuarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al crear usuario: " + e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
    }

    // ============================================
    // EDITAR USUARIO
    // ============================================

    /**
     * Muestra el formulario para editar un usuario
     */
    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        model.addAttribute("usuarioForm", usuario);
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("roles", Rol.values());

        return "usuarios/formulario";
    }

    /**
     * Procesa la actualización de un usuario
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute("usuarioForm") Usuario usuario,
            @RequestParam(value = "password", required = false) String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Si se proporciona nueva contraseña, establecerla
            if (password != null && !password.trim().isEmpty()) {
                usuario.setPasswordHash(password);
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);

            redirectAttributes.addFlashAttribute("success",
                    "Usuario actualizado correctamente: " + usuarioActualizado.getUsername());

            return "redirect:/usuarios/ver/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/usuarios/editar/" + id;
        }
    }

    // ============================================
    // ELIMINAR USUARIO
    // ============================================

    /**
     * Elimina un usuario (borrado lógico)
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Evitar que el admin se elimine a sí mismo
            if (id.equals(userDetails.getUsuario().getId())) {
                redirectAttributes.addFlashAttribute("error",
                        "No puede eliminar su propio usuario");
                return "redirect:/usuarios";
            }

            Usuario usuario = usuarioService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            String username = usuario.getUsername();

            usuarioService.eliminarUsuario(id);

            redirectAttributes.addFlashAttribute("success",
                    "Usuario desactivado correctamente: " + username);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar usuario: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }

    // ============================================
    // ACTIVAR/DESACTIVAR USUARIO
    // ============================================

    /**
     * Activa o desactiva un usuario
     */
    @PostMapping("/toggle-estado/{id}")
    public String toggleEstadoUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Evitar que el admin se desactive a sí mismo
            if (id.equals(userDetails.getUsuario().getId())) {
                redirectAttributes.addFlashAttribute("error",
                        "No puede cambiar el estado de su propio usuario");
                return "redirect:/usuarios";
            }

            Usuario usuario = usuarioService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            usuario.setActivo(!usuario.getActivo());
            usuarioService.actualizarUsuario(id, usuario);

            String estado = usuario.getActivo() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("success",
                    "Usuario " + estado + " correctamente: " + usuario.getUsername());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar estado: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }
}