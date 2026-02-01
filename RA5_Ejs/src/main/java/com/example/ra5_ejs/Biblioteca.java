package com.example.ra5_ejs;

import jakarta.xml.bind.annotation.*;

import java.util.List;

// Biblioteca.java
@XmlRootElement(name = "biblioteca")
@XmlAccessorType(XmlAccessType.FIELD)
public class Biblioteca {

    @XmlElementWrapper(name = "libros")
    @XmlElement(name = "libro")
    private List<Libro> libros;

    public Biblioteca() {}

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
}