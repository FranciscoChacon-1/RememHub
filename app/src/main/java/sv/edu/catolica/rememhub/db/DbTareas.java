package sv.edu.catolica.rememhub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import sv.edu.catolica.rememhub.RememhubBD;
import sv.edu.catolica.rememhub.Tarea;


public class DbTareas extends RememhubBD {
    Context context;

    public DbTareas(@Nullable Context context) {
        super(context);
    }

    public long insertarTarea(String titulo, String descripcion, int categoriaId, String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio) {
        long id = 0;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("titulo", titulo);
            values.put("descripcion", descripcion);
            values.put("categoria_id", categoriaId);
            values.put("fecha_cumplimiento", fechaCumplimiento);
            values.put("hora_cumplimiento", horaCumplimiento);
            values.put("hora_recordatorio", horaRecordatorio);

            Log.d("DbTareas", "Inserting task: " + values.toString());

            id = db.insert(TABLE_TAREAS, null, values);
            if (id == -1) {
                Log.e("DbTareas", "Error inserting task: Insertion failed");
            }
        } catch (Exception ex) {
            Log.e("DbTareas", "Error inserting task: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public List<Tarea> obtenerTareasPorCategoria(int categoriaId) {
        List<Tarea> listaTareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAREAS + " WHERE categoria_id = ?", new String[]{String.valueOf(categoriaId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                        String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                        boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("completada")) == 1; // Ajusta según tu base de datos
                        boolean eliminada = cursor.getInt(cursor.getColumnIndexOrThrow("eliminada")) == 1; // Recuperar "eliminada" desde la base de datos

                        // Crea la tarea con los parámetros requeridos
                        Tarea tarea = new Tarea(nombre, "Categoria", fechaCumplimiento, completada); // Ajusta "Categoria" según corresponda
                        tarea.setDescripcion(descripcion);
                        tarea.setEliminada(eliminada); // Establece si está eliminada o no

                        listaTareas.add(tarea);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close(); // Asegúrate de cerrar el cursor
            }
        }
        return listaTareas;
    }

}