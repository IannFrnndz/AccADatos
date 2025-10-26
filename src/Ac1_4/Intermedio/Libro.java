package Ac1_4.Intermedio;

public class Libro {
    // atributos
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private int anoPublicacion;
    private int numPaginas;
    private boolean disponible;
    private int prestamos;

    // constructor
    public Libro(String isbn, String titulo, String autor, String categoria, int anoPublicacion, int numPaginas, boolean disponible, int prestamos) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.anoPublicacion = anoPublicacion;
        this.numPaginas = numPaginas;
        this.disponible = disponible;
        this.prestamos = prestamos;
    }


    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getAnoPublicacion() {
        return anoPublicacion;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public int getPrestamos() {
        return prestamos;
    }
}
