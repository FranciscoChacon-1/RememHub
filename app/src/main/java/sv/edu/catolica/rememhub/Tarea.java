package sv.edu.catolica.rememhub;

public class Tarea {
    private String nombre;
    private String categoria;
    private String fecha;
    private boolean completada;

    public Tarea(String nombre, String categoria, String fecha, boolean completada) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.completada = completada;
    }

    // Métodos getter y setter
    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}
