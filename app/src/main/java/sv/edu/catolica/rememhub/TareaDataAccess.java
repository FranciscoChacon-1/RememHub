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

    public void marcarTareaComoCompletada(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("estado", 1); // 1: completada

        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = { tarea.getNombre(), tarea.getFecha() };

        database.update("Tareas", values, whereClause, whereArgs);
    }

    public List<Tarea> obtenerTareasSemana(boolean estaSemana) {
        List<Tarea> listaTareas = new ArrayList<>();
        String query;

        if (estaSemana) {
            query = "SELECT titulo, descripcion, fecha_cumplimiento, estado " +
                    "FROM Tareas WHERE fecha_cumplimiento <= date('now', '+7 days') AND estado = 0";
        } else {
            query = "SELECT titulo, descripcion, fecha_cumplimiento, estado " +
                    "FROM Tareas WHERE fecha_cumplimiento > date('now', '+7 days') AND estado = 0";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareas;
    }

    public List<Tarea> obtenerTareasNoCompletadas() {
        List<Tarea> listaTareas = new ArrayList<>();
        String query = "SELECT titulo, descripcion, fecha_cumplimiento, estado " +
                "FROM Tareas WHERE estado = 0"; // 0: no completada

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareas;
    }
    public void marcarTareaComoNoCompletada(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("estado", 0); // 0: no completada

        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = { tarea.getNombre(), tarea.getFecha() };

        database.update("Tareas", values, whereClause, whereArgs);
    }

}
