package sv.edu.catolica.rememhub.db;

import android.annotation.SuppressLint;
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

    public long insertarTarea(String titulo, String descripcion, String fechaCreacion, String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio, int categoriaId) {
        long id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("titulo", titulo);
            values.put("descripcion", descripcion);
            values.put("categoria_id", categoriaId);  // Ahora toma en cuenta categoriaId
            values.put("fecha_cumplimiento", fechaCumplimiento);
            values.put("hora_cumplimiento", horaCumplimiento);
            values.put("hora_recordatorio", horaRecordatorio);
            values.put("fecha_creacion", fechaCreacion);

            id = db.insert(TABLE_TAREAS, null, values);
        } catch (Exception ex) {
            Log.e("DbTareas", "Error inserting task: " + ex.getMessage());
        } finally {
            db.close();
        }
        return id;
    }


    public void insertarDiaRecordatorio(int tareaId, String dia) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("tarea_id", tareaId);
            values.put("dia", dia);

            db.insert("DiasRecordatorio", null, values);
        } catch (Exception ex) {
            Log.e("DbTareas", "Error inserting reminder day: " + ex.getMessage());
        } finally {
            db.close();
        }
    }

    @SuppressLint("Range")
    public List<Tarea> obtenerTareasPorCategoria(int categoriaId) {
        List<Tarea> tareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TAREAS, null, "categoria_id=?", new String[]{String.valueOf(categoriaId)}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Tarea tarea = new Tarea();
                        tarea.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        tarea.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
                        tarea.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                        tarea.setFechaCumplimiento(cursor.getString(cursor.getColumnIndex("fecha_cumplimiento")));
                        tareas.add(tarea);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return tareas;
    }
}
