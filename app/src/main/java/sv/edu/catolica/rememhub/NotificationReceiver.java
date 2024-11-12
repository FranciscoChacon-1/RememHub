package sv.edu.catolica.rememhub;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "tarea_notificacion";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Notificación recibida");

        String tareaTitulo = intent.getStringExtra("titulo");
        String tareaDescripcion = intent.getStringExtra("descripcion");
        long tareaId = intent.getLongExtra("tareaId", -1);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal de notificación si no existe (requerido a partir de Android Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cumplimiento de Tarea";
            String description = "Notificaciones de recordatorio de cumplimiento de tareas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Recordatorio Final de Tarea: " + tareaTitulo)
                .setContentText(tareaDescripcion)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Asegúrate de usar un ícono válido
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notification);

        cancelarNotificacionesRecurrentes(context, tareaId);
    }

    private void cancelarNotificacionesRecurrentes(Context context, long tareaId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int dia = Calendar.SUNDAY; dia <= Calendar.SATURDAY; dia++) {
            for (int hora = 0; hora < 24; hora++) {
                for (int minuto = 0; minuto < 60; minuto += 30) {
                    int requestCode = dia * 100 + hora * 10 + minuto;

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
        }
    }
}
