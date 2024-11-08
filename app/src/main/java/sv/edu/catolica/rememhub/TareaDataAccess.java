package sv.edu.catolica.rememhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class TareaDataAccess {

    private SQLiteDatabase db;
    private SQLiteDatabase database;

    public TareaDataAccess(Context context) {
        RememhubBD dbHelper = new RememhubBD(context);
        this.db = dbHelper.getReadableDatabase();
        this.database = dbHelper.getWritableDatabase();
    }



    public List<Tarea> obtenerTareasSemana(boolean estaSemana) {
        List<Tarea> listaTareas = new ArrayList<>();
        String query;

        if (estaSemana) {
            query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                    "FROM Tareas " +
                    "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                    "WHERE Tareas.fecha_cumplimiento <= date('now', '+7 days') AND Tareas.estado = 0";
        } else {
            query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                    "FROM Tareas " +
                    "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                    "WHERE Tareas.fecha_cumplimiento > date('now', '+7 days') AND Tareas.estado = 0";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareas;
    }





    public void eliminarTarea(Tarea tarea) {
        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = { tarea.getNombre(), tarea.getFecha() };

        database.delete("Tareas", whereClause, whereArgs);
    }

    public void marcarTareaComoEliminada(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("estado", 1); // 1: eliminada

        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = { tarea.getNombre(), tarea.getFecha() };

        database.update("Tareas", values, whereClause, whereArgs);
    }



    public List<Tarea> obtenerTareasEliminadas() {
        List<Tarea> listaTareasEliminadas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.estado = 1"; // 1: eliminada

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

    public Tarea obtenerTareaPorNombre(String nombre) {
        Tarea tarea = null;
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.titulo = ?";

        Cursor cursor = db.rawQuery(query, new String[]{nombre});

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

    // Método para restaurar tarea desde la papelera usando el nombre
    public void restaurarTarea(Tarea tarea) {
        // Eliminar la tarea de la tabla 'papelera' o cambiar su estado a no eliminada
        ContentValues values = new ContentValues();
        values.put("completada", 0);  // Asegúrate de tener un campo para marcar como completada o eliminada

        // Usar el nombre de la tarea en lugar del id para restaurarla
        db.update("tareas", values, "nombre = ?", new String[]{tarea.getNombre()});
    }

}
