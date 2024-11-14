package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

// NOTIFICACION FINAL (Fecha y Hora de cumplimiento)
public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "tarea_notificacion";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tareaTitulo = intent.getStringExtra("titulo");
        String tareaDescripcion = intent.getStringExtra("descripcion");
        long tareaId = intent.getLongExtra("tareaId", -1);
        Log.d("Notifinal", "Notificacion final recibida: "+tareaTitulo);
        // Verificar el estado de la tarea
        RememhubBD dbHelper = new RememhubBD(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Consultar estado y papelera de la tarea
            Log.d("Notifinal", "Notificacion: "+tareaTitulo+" Entro al try catch");
            Cursor cursor = db.query("Tareas", new String[]{"estado", "papelera"},
                    "id = ?", new String[]{String.valueOf(tareaId)},
                    null, null, null);
            String cursorprueba = "";
            if(cursor!= null){
                cursorprueba = "true";
            }else{
                cursorprueba ="nulo";
            }
            Log.d("Notifinal", "Notificacion: "+tareaTitulo+" cursor: "+cursorprueba);
            String movetofirst = "";
            if(cursor.moveToFirst()){
                movetofirst="true";
            }else{
                movetofirst="false";
            }
            Log.d("Notifinal", "Notificacion: "+tareaTitulo+" cursor.movetoTofirst: "+movetofirst);
            if (cursor != null && cursor.moveToFirst()) {
                Log.d("Notifinal", "Notificacion: "+tareaTitulo+"Entro al primer if");
                @SuppressLint("Range") int estado = cursor.getInt(cursor.getColumnIndex("estado"));
                @SuppressLint("Range") int papelera = cursor.getInt(cursor.getColumnIndex("papelera"));

                if (estado == 0 && papelera == 0) {  // Si no está completada ni en papelera
                    Log.d("Notifinal", "Notificacion: "+tareaTitulo+" Entro al segundo if");
                    ContentValues values = new ContentValues();
                    values.put("estado", 1);
                    Log.d("Notifinal", "Estado de "+tareaTitulo+" cambiado a : "+ values);
                    db.update("Tareas", values, "id = ?", new String[]{String.valueOf(tareaId)});

                    cancelarNotificacionesFinales(context, tareaId);
                    cancelarNotificacionesRecurrentes(context, tareaId);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        mostrarNotificacionFinal(context, tareaTitulo, tareaDescripcion);
    }

    private void mostrarNotificacionFinal(Context context, String titulo, String descripcion) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cumplimiento de Tarea";
            String description = "Notificaciones de recordatorio de cumplimiento de tareas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Recordatorio Final de Tarea: " + titulo)
                .setContentText(descripcion)
                .setSmallIcon(R.drawable.icon_noti)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        ContentValues values = new ContentValues();

        values.put("estado", 1);
        Log.d("Notifinal", "MOSTRARNOTIFICACIONFINALSe recibio y cambio el estado a: "+values+" de noti: "+ titulo);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notification);
    }


    private void cancelarNotificacionesFinales(Context context, long tareaId) {
        // Obtener los datos de la tarea desde la base de datos
        RememhubBD dbHelper = new RememhubBD(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Tareas",
                new String[]{"fecha_cumplimiento", "hora_cumplimiento"},
                "id = ?", new String[]{String.valueOf(tareaId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String fechaCumplimiento = cursor.getString(cursor.getColumnIndex("fecha_cumplimiento"));
            @SuppressLint("Range") String horaCumplimiento = cursor.getString(cursor.getColumnIndex("hora_cumplimiento"));

            // Cerrar el cursor después de usarlo
            cursor.close();

            // Convertir fecha_cumplimiento y hora_cumplimiento en valores para la alarma
            Calendar calendar = Calendar.getInstance();
            // Aquí debes convertir fechaCumplimiento y horaCumplimiento a los valores adecuados (Año, Mes, Día, Hora, Minuto)
            // Se asume que las fechas están en formato "yyyy-MM-dd" y "HH:mm"
            String[] fechaParts = fechaCumplimiento.split("-");
            String[] horaParts = horaCumplimiento.split(":");
            calendar.set(Calendar.YEAR, Integer.parseInt(fechaParts[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(fechaParts[1]) - 1);  // Meses en Java comienzan desde 0
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaParts[2]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaParts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(horaParts[1]));
            calendar.set(Calendar.SECOND, 0);

            int requestCode = (int) (tareaId + calendar.getTimeInMillis());

            // Cancelar la notificación final
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }


    @SuppressLint("Range")
    private void cancelarNotificacionesRecurrentes(Context context, long tareaId) {
        // Obtener los datos de la tarea desde la base de datos
        RememhubBD dbHelper = new RememhubBD(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Obtener la hora de recordatorio
        Cursor cursor = db.query("Tareas",
                new String[]{"hora_recordatorio"},
                "id = ?", new String[]{String.valueOf(tareaId)},
                null, null, null);

        String horaRecordatorio = null;
        if (cursor != null && cursor.moveToFirst()) {
            horaRecordatorio = cursor.getString(cursor.getColumnIndex("hora_recordatorio"));
            cursor.close();
        }

        // Obtener los días de recordatorio asociados a la tarea
        Cursor diasCursor = db.query("DiasRecordatorio",
                new String[]{"dia"},
                "tarea_id = ?", new String[]{String.valueOf(tareaId)},
                null, null, null);

        if (diasCursor != null && diasCursor.getCount() > 0) {
            // Recorrer todos los días de recordatorio
            while (diasCursor.moveToNext()) {
                @SuppressLint("Range") String diaRecordatorio = diasCursor.getString(diasCursor.getColumnIndex("dia"));

                // Convertir hora_recordatorio en un formato adecuado
                String[] horaParts = horaRecordatorio.split(":");
                int hora = Integer.parseInt(horaParts[0]);
                int minuto = Integer.parseInt(horaParts[1]);

                // Usar Calendar para definir la hora y el día de la notificación
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hora);
                calendar.set(Calendar.MINUTE, minuto);
                calendar.set(Calendar.SECOND, 0);

                // Configurar el día de la semana de la notificación (basado en "diaRecordatorio")
                switch (diaRecordatorio) {
                    case "Lunes":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        break;
                    case "Martes":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        break;
                    case "Miércoles":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        break;
                    case "Jueves":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        break;
                    case "Viernes":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        break;
                    case "Sábado":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        break;
                    case "Domingo":
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        break;
                }

                // Crear un identificador único para la notificación
                int requestCode = (int) (tareaId + calendar.getTimeInMillis());

                // Cancelar la notificación recurrente
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, ReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
            }
            diasCursor.close();
        }
    }

}
