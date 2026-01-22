package com.example.crud.repository;



import com.example.crud.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de productos.
 * JpaRepository proporciona métodos CRUD automáticamente.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ═══════════════════════════════════════════════════════════════════
    // QUERY METHODS: Spring genera la consulta a partir del nombre
    // ═══════════════════════════════════════════════════════════════════

    // Buscar por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar activos
    List<Producto> findByActivoTrue();

    // Buscar por nombre (contiene, ignora mayúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por rango de precio
    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);

    // Buscar por categoría y activo, ordenados por nombre
    List<Producto> findByCategoriaAndActivoTrueOrderByNombreAsc(String categoria);

    // Buscar con stock bajo
    List<Producto> findByStockLessThan(Integer cantidad);

    // ═══════════════════════════════════════════════════════════════════
    // CONSULTAS JPQL PERSONALIZADAS
    // ═══════════════════════════════════════════════════════════════════

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.fechaCreacion DESC")
    List<Producto> findProductosActivosRecientes();

    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true ORDER BY p.categoria")
    List<String> findCategoriasActivas();

    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:categoria IS NULL OR p.categoria = :categoria) AND " +
            "p.activo = true")
    List<Producto> buscarConFiltros(@Param("nombre") String nombre,
                                    @Param("categoria") String categoria);
}