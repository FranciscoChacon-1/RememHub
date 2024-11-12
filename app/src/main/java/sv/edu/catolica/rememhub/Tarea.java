package sv.edu.catolica.rememhub;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tarea {
    private int id;
    private String titulo;
    private String categoria;
    private String fechaCumplimiento;  // Cambié 'fecha' por 'fechaCumplimiento'
    private boolean completada;
    private String descripcion;
    private boolean eliminada;
    private String diasRecordatorio;
    private int categoriaId;
    private String fechaCreacion;
    private String horaCumplimiento;
    private String horaRecordatorio;
    private Boolean estado;

    // Constructor con días de recordatorio
    public Tarea(String nombre, String categoria, String fechaCumplimiento, boolean completada, String diasRecordatorio) {
        this.titulo = nombre;
        this.categoria = categoria;
        this.fechaCumplimiento = fechaCumplimiento;
        this.completada = completada;
        this.diasRecordatorio = diasRecordatorio;
    }

    // Constructor sin días de recordatorio
    public Tarea(String nombre, String categoria, String fechaCumplimiento, boolean completada) {
        this.titulo = nombre;
        this.categoria = categoria;
        this.fechaCumplimiento = fechaCumplimiento;
        this.completada = completada;
    }

    public Tarea(int id, String titulo, String fechaCumplimiento, String categoria, String cumplimiento, String horaCumplimiento, String horaRecordatorio) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCumplimiento = fechaCumplimiento;
        this.categoria=categoria;
        this.fechaCumplimiento = cumplimiento;
        this.horaCumplimiento = horaCumplimiento;
        this.horaRecordatorio = horaRecordatorio;
    }

    public Tarea(int id, String titulo, String fechaCumplimiento, String categoria, String fechaCumplimiento1) {
        this.id = id;
        this.titulo = titulo;
        this.categoria=categoria;
        this.fechaCumplimiento = fechaCumplimiento;
        this.categoria = categoria;
        this.fechaCumplimiento = fechaCumplimiento1;

    }

    public Tarea(String titulo, String descripcion, String categoria, String fechaCreacion, String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio, boolean completada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
        this.fechaCumplimiento = fechaCumplimiento;
        this.horaCumplimiento = horaCumplimiento;
        this.horaRecordatorio = horaRecordatorio;
        this.estado = completada;
    }

    public Tarea() {

    }

    public Tarea(int id, String titulo, String categoria, String fechaCumplimiento, boolean completada) {
        this.id = id;
        this.titulo = titulo;
        this.fechaCumplimiento = fechaCumplimiento;
        this.categoria=categoria;
        this.estado=completada;
    }

    // Método para convertir la fecha de String a Date
    public Date getFechaCumplimiento() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(fechaCumplimiento);  // Convierte el String a Date
        } catch (ParseException e) {
            e.printStackTrace();
            return null;  // En caso de error, retorna null
        }
    }

    public String getFechaCumplimientoString() {
        return fechaCumplimiento;  // Devolver directamente la fecha como String
    }

    // Métodos getter y setter para los demás atributos
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFecha() {
        return fechaCumplimiento;
    }

    // Agregar el setter para fechaCumplimiento
    public void setFechaCumplimiento(String fechaCumplimiento) {
        this.fechaCumplimiento = fechaCumplimiento;
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

    // Método getter para obtener la ID de la categoría
    public int getCategoriaId() {
        return categoriaId;
    }

    // Método getter para obtener la hora de cumplimiento
    public String getHoraCumplimiento() {
        return horaCumplimiento;
    }

    // Método getter para obtener la hora de recordatorio
    public String getHoraRecordatorio() {
        return horaRecordatorio;
    }

    // Método getter para obtener la fecha de creación
    public String getFechaCreacion() {
        return fechaCreacion;
    }
}
