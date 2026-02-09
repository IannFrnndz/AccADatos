package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public String listarUsuarios(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuarioActual = userDetails.getUsuario();
        List<Usuario> usuarios = usuarioService.listarTodos();

        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("usuarios", usuarios);

        return "usuarios/lista";
    }

    /**
     * Mostrar formulario para crear nuevo usuario
     */
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuarioActual = userDetails.getUsuario();
        Usuario nuevoUsuario = new Usuario();

        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("nuevoUsuario", nuevoUsuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("esNuevo", true);

        return "usuarios/formulario";
    }

    /**
     * Procesar la creacion de un nuevo usuario
     */
    @PostMapping("/crear")
    public String crearUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.crear(usuario, password);
            redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
        return "redirect:/usuarios";
    }

    /**
     * Mostrar formulario para editar usuario
     */
    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuarioActual = userDetails.getUsuario();
            Usuario usuarioEditar = usuarioService.obtenerPorId(id);

            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("nuevoUsuario", usuarioEditar);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("esNuevo", false);

            return "usuarios/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios";
        }
    }

    /**
     * Procesar la actualizacion de un usuario
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuario.setId(id);

            // Si se proporciona nueva contraseña, cambiarla
            if (password != null && !password.trim().isEmpty()) {
                usuarioService.cambiarPassword(id, password);
            }

            usuarioService.actualizar(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Cambiar estado de un usuario (activar/desactivar)
     */
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.cambiarEstado(id, activo);
            String mensaje = activo ? "Usuario activado" : "Usuario desactivado";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Eliminar un usuario
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // No permitir que un admin se elimine a si mismo
            if (userDetails.getUsuario().getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "No puedes eliminarte a ti mismo");
                return "redirect:/usuarios";
            }

            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}