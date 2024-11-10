package sv.edu.catolica.rememhub;

public class Tarea {
    private int id;
    private String nombre;
    private String categoria;
    private String fecha;
    private boolean completada;
    private String descripcion;
    private boolean eliminada;

    // Constructor
    public Tarea(String nombre, String categoria, String fecha, boolean completada) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.completada = completada;
    }

    public Tarea() {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.completada = completada;

    }


    public boolean isEliminada() {
        return eliminada;
    }

    public void setEliminada(boolean eliminada) {
        this.eliminada = eliminada;
    }
    // Métodos getter y setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
