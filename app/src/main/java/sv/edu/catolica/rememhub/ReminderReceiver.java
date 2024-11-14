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

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

// NOTIS DE RECORDATORIO (recurrentes)
public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "tareas_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tareaTitulo = intent.getStringExtra("titulo");
        String tareaDescripcion = intent.getStringExtra("descripcion");
        long tareaId = intent.getLongExtra("tareaId", -1);

        // Verificar el estado y papelera directamente desde la base de datos
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            RememhubBD dbHelper = new RememhubBD(context);
            db = dbHelper.getReadableDatabase();
            cursor = db.query("Tareas", new String[]{"estado", "papelera"},
                    "id = ?", new String[]{String.valueOf(tareaId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int estado = cursor.getInt(cursor.getColumnIndex("estado"));
                @SuppressLint("Range") int papelera = cursor.getInt(cursor.getColumnIndex("papelera"));

                if (estado == 1 || papelera == 1) {
                    cancelarNotificacion(context, tareaId);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        mostrarNotificacion(context, tareaTitulo, tareaDescripcion, tareaId);
    }

    private void mostrarNotificacion(Context context, String titulo, String descripcion, long tareaId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tareas Recordatorio";
            String description = "Notificaciones de recordatorio de tareas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(descripcion) // Asegúrate de que la descripción se incluya aquí.
                .setSmallIcon(R.drawable.icon_recordatorio)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Generar un ID único para la notificación basada en tareaId para que no se duplique
        int notificationId = (int) (tareaId); // Mantenerlo único por tarea

        notificationManager.notify(notificationId, notification);
    }

    private void cancelarNotificacion(Context context, long tareaId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = (int) (tareaId);
        notificationManager.cancel(notificationId);
    }
}