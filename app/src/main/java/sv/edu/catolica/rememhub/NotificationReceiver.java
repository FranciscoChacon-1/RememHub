package sv.edu.catolica.rememhub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "tareas_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tareaTitulo = intent.getStringExtra("titulo");
        String tareaDescripcion = intent.getStringExtra("descripcion");

        Log.d("NotificationReceiver", "Titulo: " + tareaTitulo + ", Descripcion: " + tareaDescripcion);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal de notificación si no existe (requerido a partir de Android Oreo)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Tareas Recordatorio";
            String description = "Notificaciones de recordatorio de tareas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Recordatorio: " + tareaTitulo)
                .setContentText(tareaDescripcion)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Mostrar la notificación con un ID único
        int notificationId = (int) System.currentTimeMillis(); // Generar un ID único
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }

    }
}
