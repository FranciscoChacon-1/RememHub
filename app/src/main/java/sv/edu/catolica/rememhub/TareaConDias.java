package sv.edu.catolica.rememhub;

public class TareaConDias {
    private int id;
    private String titulo;
    private String categoria;
    private String fechaCreacion;
    private String diasRecordatorio;  // Días concatenados, separados por coma

    // Constructor, getters y setters
    public TareaConDias(int id, String titulo, String categoria, String fechaCreacion, String diasRecordatorio) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
        this.diasRecordatorio = diasRecordatorio;
    }

    public String getDiasRecordatorio() {
        return diasRecordatorio;
    }

    // Otros getters y setters
}
