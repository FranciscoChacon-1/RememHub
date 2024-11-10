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
                        Tarea tarea = new Tarea();
                        tarea.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id"))); // Use getColumnIndexOrThrow
                        tarea.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("titulo"))); // Use getColumnIndexOrThrow
                        tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                        tarea.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"))); // Retrieve the date
                        listaTareas.add(tarea);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close(); // Ensure the cursor is closed
            }
        }
        return listaTareas;
    }
}