package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class menu_inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio);
    }

    public void AnadirTarea(View view) {
        Intent intent = new Intent(this, Añadirtarea.class); // Redirige a Añadirtarea
        startActivity(intent);
    }

    public void DetalleTarea(View view) {
        Intent intent = new Intent(this, detalles_de_tarea.class); // Redirige a detalles_de_tarea
        startActivity(intent);
    }

    public void Historial(View view) {
        Intent intent = new Intent(this, activity_historial.class); // Redirige a activity_historial
        startActivity(intent);
    }

    public void Categorias(View view) {
        Intent intent = new Intent(this, MainActivity.class); // Redirige a MainActivity2
        startActivity(intent);
    }

    public void Papelera(View view) {
        Intent intent = new Intent(this, PapeleraActivity.class); // Redirige a PapeleraActivity
        startActivity(intent);
    }

    public void Configuracion(View view) {
        Intent intent = new Intent(this, activity_configuracion.class); // Redirige a activity_configuracion
        startActivity(intent);
    }

    public void Resumen(View view) {
        Intent intent = new Intent(this, activity_resumen.class); // Redirige a activity_resumen
        startActivity(intent);
    }

    public void Inicio(View view) {
        Intent intent = new Intent(this, tareas_pendientes_inicio.class); // Redirige a tareas_pendientes_inicio
        startActivity(intent);
    }
}
