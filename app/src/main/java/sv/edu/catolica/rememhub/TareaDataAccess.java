package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TareaDataAccess {

    private SQLiteDatabase db;
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

    public TareaDataAccess(Context context) {
        RememhubBD dbHelper = new RememhubBD(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public List<Tarea> obtenerTareasSemana() {
        List<Tarea> listaTareasFuturas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.fecha_cumplimiento >= date('now') AND Tareas.estado = 0"; // Solo tareas no completadas y con fecha futura

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                // Añadir la tarea sin convertir la fecha
                listaTareasFuturas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasFuturas;
    }

    public List<Tarea> obtenerTareasExpiradas() {
        List<Tarea> listaTareasExpiradas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.fecha_cumplimiento < date('now') AND Tareas.estado = 0"; // Solo tareas vencidas y no completadas

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                // Añadir la tarea sin convertir la fecha
                listaTareasExpiradas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasExpiradas;
    }


    // Método para obtener tarea por nombre
    public Tarea obtenerTareaPorNombre(String nombreTarea) {
        Tarea tarea = null;
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.titulo = ?";

        Cursor cursor = db.rawQuery(query, new String[]{nombreTarea});

        if (cursor.moveToFirst()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
            boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

            tarea = new Tarea(titulo, categoria, fecha, completada);
        }
        cursor.close();
        return tarea;
    }

    // Método para eliminar tarea
    public void eliminarTarea(Tarea tarea) {
        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = { tarea.getNombre(), tarea.getFecha() };

        db.delete("Tareas", whereClause, whereArgs);
    }

    public void marcarTareaComoEliminada(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("estado", 1); // 1: marcado como eliminado
        db.update("Tareas", values, "titulo = ? AND fecha_cumplimiento = ?",
                new String[]{tarea.getNombre(), tarea.getFecha()});
    }




    // Método para obtener las tareas eliminadas
    public List<Tarea> obtenerTareasEliminadas() {
        List<Tarea> listaTareasEliminadas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.estado = 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareasEliminadas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasEliminadas;
    }




}
