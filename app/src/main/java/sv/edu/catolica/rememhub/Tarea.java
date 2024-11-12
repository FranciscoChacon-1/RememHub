package sv.edu.catolica.rememhub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tarea {
    private int id;
    private String nombre;
    private String categoria;
    private String fecha;  // La fecha es un String
    private boolean completada;
    private String descripcion;
    private boolean eliminada;
    private String diasRecordatorio;

    // Constructor con días de recordatorio
    public Tarea(String nombre, String categoria, String fecha, boolean completada, String diasRecordatorio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.completada = completada;
        this.diasRecordatorio = diasRecordatorio;
    }

    // Constructor sin días de recordatorio
    public Tarea(String nombre, String categoria, String fecha, boolean completada) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.completada = completada;
    }

    //Comit José
    public Tarea() {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.completada = completada;

    }


    // Para el historial
    public Tarea(int id, String nombre, String categoria, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
        this.completada = false;  // Valor predeterminado, o puedes dejarlo a tu criterio
        this.eliminada = false;   // Valor predeterminado
        this.diasRecordatorio = "";  // Valor predeterminado
    }


    public Tarea(int id, String nombre, String categoria, String fecha, String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio) {
    }

    // Método para convertir la fecha de String a Date
    public Date getFechaCumplimiento() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(fecha);  // Convierte el String a Date
        } catch (ParseException e) {
            e.printStackTrace();
            return null;  // En caso de error, retorna null
        }
    }

    // Métodos getter y setter para los demás atributos
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public String getDiasRecordatorio() {
        return diasRecordatorio;
    }

    public boolean isEliminada() {
        return eliminada;
    }

    public void setEliminada(boolean eliminada) {
        this.eliminada = eliminada;
    }

    public void setDiasRecordatorio(String diasRecordatorio) {
        this.diasRecordatorio = diasRecordatorio;
    }

}
