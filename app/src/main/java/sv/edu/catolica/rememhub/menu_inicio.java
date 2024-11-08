package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu_inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_inicio);

    }

    public void AnadirTarea(View view) {
        /*Intent intent;
        intent = new Intent(this, menu_inicio.class);
        startActivity(intent);*/
    }

    public void DetalleTarea(View view) {
        Intent intent;
        intent = new Intent(this, detalles_de_tarea.class);
        startActivity(intent);
    }

    public void Historial(View view) {
        Intent intent;
        intent = new Intent(this, activity_historial.class);
        startActivity(intent);
    }

    public void Categorias(View view) {
        /*Intent intent;
        intent = new Intent(this, menu_inicio.class);
        startActivity(intent);*/
    }

    public void Papelera(View view) {
        Intent intent;
        intent = new Intent(this, PapeleraActivity.class);
        startActivity(intent);
    }

    public void Configuracion(View view) {
        Intent intent;
        intent = new Intent(this, activity_configuracion.class);
        startActivity(intent);
    }

    public void Resumen(View view) {
        Intent intent;
        intent = new Intent(this, activity_resumen.class);
        startActivity(intent);
    }
    public void Inicio(View view) {
        Intent intent;
        intent = new Intent(this, tareas_pendientes_inicio.class);
        startActivity(intent);
    }
}