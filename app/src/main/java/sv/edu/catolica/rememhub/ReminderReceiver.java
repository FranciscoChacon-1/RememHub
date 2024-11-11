package sv.edu.catolica.rememhub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "tareas_channel"; // Usamos un solo canal

    @Override
    public void onReceive(Context context, Intent intent) {
        String tareaTitulo = intent.getStringExtra("titulo");  // Cambié el nombre a "titulo" para que coincida con el de Añadirtarea.java
        String tareaDescripcion = intent.getStringExtra("descripcion"); // Igual aquí

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
                .setContentTitle(tareaTitulo)
                .setContentText(tareaDescripcion)
                .setSmallIcon(R.drawable.ic_launcher_background)  // Asegúrate de que el icono exista en los recursos
                .build();

        // Usar un ID único basado en el tiempo para evitar sobrescribir notificaciones
        int notificationId = (int) System.currentTimeMillis(); // ID único
        notificationManager.notify(notificationId, notification);
    }
}
