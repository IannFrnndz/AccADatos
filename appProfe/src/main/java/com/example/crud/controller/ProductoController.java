package com.example.crud.controller;

import com.example.crud.entity.Producto;
import com.example.crud.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web para gestión de productos.
 * Maneja las peticiones HTTP y devuelve vistas Thymeleaf.
 */
@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ═══════════════════════════════════════════════════════════════════
    // LISTAR PRODUCTOS
    // GET /productos
    // ═══════════════════════════════════════════════════════════════════
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            Model model) {

        // Obtener productos (con o sin filtros)
        var productos = productoService.buscarConFiltros(nombre, categoria);

        // Pasar datos a la vista
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("filtroNombre", nombre);
        model.addAttribute("filtroCategoria", categoria);

        return "productos/lista";  // → templates/productos/lista.html
    }

    // ═══════════════════════════════════════════════════════════════════
    // VER DETALLE DE PRODUCTO
    // GET /productos/{id}
    // ═══════════════════════════════════════════════════════════════════
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        return productoService.buscarPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    return "productos/detalle";
                })
                .orElse("redirect:/productos");  // Si no existe, volver a lista
    }

    // ═══════════════════════════════════════════════════════════════════
    // FORMULARIO NUEVO PRODUCTO
    // GET /productos/nuevo
    // ═══════════════════════════════════════════════════════════════════
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("titulo", "Nuevo Producto");
        model.addAttribute("accion", "/productos/guardar");
        return "productos/formulario";
    }

    // ═══════════════════════════════════════════════════════════════════
    // FORMULARIO EDITAR PRODUCTO
    // GET /productos/editar/{id}
    // ═══════════════════════════════════════════════════════════════════
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        return productoService.buscarPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("categorias", productoService.obtenerCategorias());
                    model.addAttribute("titulo", "Editar Producto");
                    model.addAttribute("accion", "/productos/guardar");
                    return "productos/formulario";
                })
                .orElse("redirect:/productos");
    }

    // ═══════════════════════════════════════════════════════════════════
    // GUARDAR PRODUCTO (Crear o Actualizar)
    // POST /productos/guardar
    // ═══════════════════════════════════════════════════════════════════
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Si hay errores de validación, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("titulo", producto.getId() == null ? "Nuevo Producto" : "Editar Producto");
            model.addAttribute("accion", "/productos/guardar");
            return "productos/formulario";
        }

        // Guardar producto
        productoService.guardar(producto);

        // Mensaje flash de éxito
        redirectAttributes.addFlashAttribute("mensaje",
                producto.getId() == null ? "Producto creado correctamente" : "Producto actualizado correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        return "redirect:/productos";
    }

    // ═══════════════════════════════════════════════════════════════════
    // ELIMINAR PRODUCTO (Borrado lógico)
    // POST /productos/eliminar/{id}
    // ═══════════════════════════════════════════════════════════════════
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
        return "redirect:/productos";
    }
}