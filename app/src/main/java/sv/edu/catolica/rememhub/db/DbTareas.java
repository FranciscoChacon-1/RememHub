package sv.edu.catolica.rememhub.db;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sv.edu.catolica.rememhub.NotificationReceiver;
import sv.edu.catolica.rememhub.RememhubBD;
import sv.edu.catolica.rememhub.ReminderReceiver;
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


    public void eliminarAlarmaTarea(int tareaId, Context context) {
        // Cancelar la notificación de cumplimiento
        Intent intentCumplimiento = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntentCumplimiento = PendingIntent.getBroadcast(
                context, tareaId, intentCumplimiento, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE); // Solo FLAG_NO_CREATE
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pendingIntentCumplimiento != null) {
            alarmManager.cancel(pendingIntentCumplimiento);
        }

        // Cancelar las notificaciones de recordatorio
        Cursor cursor = null;
        try {
            RememhubBD dbHelper = new RememhubBD(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT dia FROM DiasRecordatorio WHERE tarea_id = ?", new String[]{String.valueOf(tareaId)});
            while (cursor.moveToNext()) {
                String dia = cursor.getString(0);
                Intent intentRecordatorio = new Intent(context, ReminderReceiver.class);
                // Aquí, asegúrate de usar un flag adecuado
                PendingIntent pendingIntentRecordatorio = PendingIntent.getBroadcast(
                        context, tareaId + dia.hashCode(), intentRecordatorio, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE); // Solo FLAG_NO_CREATE
                if (pendingIntentRecordatorio != null) {
                    alarmManager.cancel(pendingIntentRecordatorio);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Esta función guardará los días de recordatorio para una tarea específica.
    public void guardarAlarmas(int tareaId, List<String> dias, String horaRecordatorio) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (String dia : dias) {
                ContentValues values = new ContentValues();
                values.put("tarea_id", tareaId);
                values.put("dia", dia);
                values.put("hora_recordatorio", horaRecordatorio);
                db.insert("DiasRecordatorio", null, values);
            }
        } catch (Exception ex) {
            Log.e("DbTareas", "Error saving reminder alarms: " + ex.getMessage());
        } finally {
            db.close();
        }
    }
}
