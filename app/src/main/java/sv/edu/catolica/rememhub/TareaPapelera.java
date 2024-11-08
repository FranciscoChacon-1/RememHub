package sv.edu.catolica.rememhub;

public class TareaPapelera {
    private String nombre;
    private String categoria;
    private String fecha;

    public TareaPapelera(String nombre, String categoria, String fecha) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }
}

