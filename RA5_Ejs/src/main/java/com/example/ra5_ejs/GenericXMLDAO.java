package com.example.ra5_ejs;
import java.util.List;
import java.util.Optional;

// Interface generica
public interface GenericXMLDAO<T> {
    void guardar(T entity) throws Exception;
    Optional<T> buscarPorId(String id) throws Exception;
    List<T> buscarTodos() throws Exception;
    void actualizar(T entity) throws Exception;
    void eliminar(String id) throws Exception;
}