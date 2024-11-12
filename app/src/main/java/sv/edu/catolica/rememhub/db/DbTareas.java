package sv.edu.catolica.rememhub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sv.edu.catolica.rememhub.RememhubBD;
import sv.edu.catolica.rememhub.Tarea;


public class DbTareas extends RememhubBD {
    Context context;

    public DbTareas(@Nullable Context context) {
        super(context);
    }

    public long insertarTarea(String titulo, String descripcion, int categoriaId, String fechaCreacion, String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio) {
        long id = 0;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Insertamos los valores de la tarea
            values.put("titulo", titulo);
            values.put("descripcion", descripcion);
            values.put("categoria_id", categoriaId);
            values.put("fecha_cumplimiento", fechaCumplimiento);
            values.put("hora_cumplimiento", horaCumplimiento);
            values.put("hora_recordatorio", horaRecordatorio);
            values.put("fecha_creacion", fechaCreacion);  // Esto es el campo donde insertas la fecha de creación.

            Log.d("DbTareas", "Inserting task: " + values.toString());

            // Insertamos la tarea
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

        // Ajuste: Asegúrate de consultar solo desde la tabla Tareas
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAREAS +
                        " WHERE categoria_id = ? AND estado = 0", // estado 0 para tareas activas, ajusta según corresponda
                new String[]{String.valueOf(categoriaId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                        String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                        boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                        // Crear tarea y agregarla a la lista
                        Tarea tarea = new Tarea(nombre, "Categoria", fechaCumplimiento, completada);
                        tarea.setDescripcion(descripcion);
                        listaTareas.add(tarea);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return listaTareas;
    }


    public void eliminarDiasRecordatorio(int tareaId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // Eliminar los días de recordatorio asociados a la tarea
            int rowsDeleted = db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(tareaId)});

            if (rowsDeleted > 0) {
                Log.d("DbTareas", "Días de recordatorio eliminados correctamente para la tarea con ID: " + tareaId);
            } else {
                Log.d("DbTareas", "No se encontraron días de recordatorio para la tarea con ID: " + tareaId);
            }
        } catch (Exception ex) {
            Log.e("DbTareas", "Error al eliminar los días de recordatorio: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


}