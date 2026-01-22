package com.example.crud.service;

import com.example.crud.entity.Producto;
import com.example.crud.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para productos.
 * Actúa como intermediario entre Controller y Repository.
 */
@Service
@RequiredArgsConstructor  // Inyección de dependencias por constructor (Lombok)
@Transactional           // Todas las operaciones son transaccionales
public class ProductoService {

    private final ProductoRepository productoRepository;

    // ═══════════════════════════════════════════════════════════════════
    // OPERACIONES DE LECTURA
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Obtiene todos los productos activos.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Obtiene todos los productos (incluidos inactivos) - para admin.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarTodosAdmin() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Busca productos por nombre.
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene productos por categoría.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrueOrderByNombreAsc(categoria);
    }

    /**
     * Obtiene la lista de categorías disponibles.
     */
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return productoRepository.findCategoriasActivas();
    }

    /**
     * Busca con filtros opcionales.
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarConFiltros(String nombre, String categoria) {
        return productoRepository.buscarConFiltros(
                nombre != null && !nombre.isBlank() ? nombre : null,
                categoria != null && !categoria.isBlank() ? categoria : null
        );
    }

    // ═══════════════════════════════════════════════════════════════════
    // OPERACIONES DE ESCRITURA
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Guarda un nuevo producto o actualiza uno existente.
     */
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto (borrado lógico).
     */
    public void eliminar(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(false);
            productoRepository.save(producto);
        });
    }

    /**
     * Elimina un producto permanentemente (borrado físico).
     * CUIDADO: Esta operación no se puede deshacer.
     */
    public void eliminarPermanente(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Reactiva un producto eliminado.
     */
    public void reactivar(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(true);
            productoRepository.save(producto);
        });
    }

    /**
     * Actualiza el stock de un producto.
     */
    public void actualizarStock(Long id, Integer cantidad) {
        productoRepository.findById(id).ifPresent(producto -> {
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
        });
    }
}