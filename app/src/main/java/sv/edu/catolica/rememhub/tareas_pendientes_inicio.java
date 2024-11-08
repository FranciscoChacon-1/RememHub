package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class tareas_pendientes_inicio extends AppCompatActivity {


    private RecyclerView recyclerViewEstaSemana;
    private RecyclerView recyclerViewSiguienteSemana;
    private TareaAdapter tareaAdapterEstaSemana;
    private TareaAdapter tareaAdapterSiguienteSemana;
    private List<Tarea> listaTareasEstaSemana;
    private List<Tarea> listaTareasSiguienteSemana;
    private RememhubBD dbHelper;

    private TextView mensajeEstaSemana;
    private TextView mensajeSiguienteSemana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_pendientes_inicio);

        // Inicializar los RecyclerViews
        recyclerViewEstaSemana = findViewById(R.id.recyclerViewEstaSemana);
        recyclerViewSiguienteSemana = findViewById(R.id.recyclerViewSiguienteSemana);

        // Configurar el LayoutManager para los RecyclerViews
        recyclerViewEstaSemana.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSiguienteSemana.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar los TextViews para los mensajes
        mensajeEstaSemana = findViewById(R.id.mensajeEstaSemana);
        mensajeSiguienteSemana = findViewById(R.id.mensajeSiguienteSemana);

        // Inicializar el acceso a la base de datos
        dbHelper = new RememhubBD(this);

        // Obtener las tareas de la base de datos
        TareaDataAccess tareaDataAccess = new TareaDataAccess(this);

        // Cargar tareas para esta semana y la siguiente semana
        listaTareasEstaSemana = tareaDataAccess.obtenerTareasSemana(true);  // Tareas de esta semana
        listaTareasSiguienteSemana = tareaDataAccess.obtenerTareasSemana(false);  // Tareas de la siguiente semana

        // Actualizar las vistas después de cargar las tareas
        actualizarVistas();
    }

    // Método para actualizar las vistas de los RecyclerView y los mensajes
    private void actualizarVistas() {
        // Comprobar si hay tareas para esta semana
        if (!listaTareasEstaSemana.isEmpty()) {
            tareaAdapterEstaSemana = new TareaAdapter(this, listaTareasEstaSemana);
            recyclerViewEstaSemana.setAdapter(tareaAdapterEstaSemana);
            recyclerViewEstaSemana.setVisibility(View.VISIBLE);
            mensajeEstaSemana.setVisibility(View.GONE);  // Ocultar mensaje si hay tareas
        } else {
            mensajeEstaSemana.setVisibility(View.VISIBLE);  // Mostrar mensaje si no hay tareas
            mensajeEstaSemana.setText("No hay tareas para esta semana.");
            recyclerViewEstaSemana.setVisibility(View.GONE);  // Ocultar RecyclerView si no hay tareas
        }

        // Comprobar si hay tareas para la siguiente semana
        if (!listaTareasSiguienteSemana.isEmpty()) {
            tareaAdapterSiguienteSemana = new TareaAdapter(this, listaTareasSiguienteSemana);
            recyclerViewSiguienteSemana.setAdapter(tareaAdapterSiguienteSemana);
            recyclerViewSiguienteSemana.setVisibility(View.VISIBLE);
            mensajeSiguienteSemana.setVisibility(View.GONE);  // Ocultar mensaje si hay tareas
        } else {
            mensajeSiguienteSemana.setVisibility(View.VISIBLE);  // Mostrar mensaje si no hay tareas
            mensajeSiguienteSemana.setText("No hay tareas para la siguiente semana.");
            recyclerViewSiguienteSemana.setVisibility(View.GONE);  // Ocultar RecyclerView si no hay tareas
        }
    }


    // Método para dirigir al usuario al menú de inicio
    public void Dirigir(View view) {
        Intent intent;
        intent = new Intent(this, menu_inicio.class);
        startActivity(intent);
    }
}
