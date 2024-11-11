package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class tareas_pendientes_inicio extends AppCompatActivity {

    private RecyclerView recyclerViewProximas;
    private RecyclerView recyclerViewExpiradas;
    private TareaAdapter tareaAdapterProximas;
    private TareaAdapter tareaAdapterExpiradas;
    private List<Tarea> listaTareasProximas;
    private List<Tarea> listaTareasExpiradas;
    private RememhubBD dbHelper;

    private TextView mensajeProximas;
    private TextView mensajeExpiradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_pendientes_inicio);

        // Inicializar los RecyclerViews
        recyclerViewProximas = findViewById(R.id.recyclerViewProximas);
        recyclerViewExpiradas = findViewById(R.id.recyclerViewExpiradas);

        // Configurar el LayoutManager para los RecyclerViews
        recyclerViewProximas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpiradas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar los TextViews para los mensajes
        mensajeProximas = findViewById(R.id.mensajeProximas);
        mensajeExpiradas = findViewById(R.id.mensajeExpiradas);

        // Inicializar el acceso a la base de datos
        dbHelper = new RememhubBD(this);

        // Ejecutar la carga de datos en segundo plano
        new CargarTareasAsync().execute();
    }

    // Clase AsyncTask para cargar las tareas en segundo plano
    private class CargarTareasAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Obtener las tareas de la base de datos en segundo plano
            TareaDataAccess tareaDataAccess = new TareaDataAccess(tareas_pendientes_inicio.this);
            listaTareasProximas = Collections.unmodifiableList(tareaDataAccess.obtenerTareasSemana());  // Tareas próximas
            listaTareasExpiradas = tareaDataAccess.obtenerTareasExpiradas();  // Tareas expiradas
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Actualizar las vistas después de cargar las tareas
            actualizarVistas();
        }
    }

    // Método para actualizar las vistas de los RecyclerView y los mensajes
    private void actualizarVistas() {
        // Actualizar todas las vistas con un solo método
        actualizarVista(recyclerViewProximas, mensajeProximas, listaTareasProximas, getString(R.string.mensajeProximas));
        actualizarVista(recyclerViewExpiradas, mensajeExpiradas, listaTareasExpiradas, getString(R.string.mensajeExpiradas));
    }

    // Método general para actualizar las vistas de cada RecyclerView
    private void actualizarVista(RecyclerView recyclerView, TextView mensaje, List<Tarea> listaTareas, String mensajeVacio) {
        if (listaTareas != null && !listaTareas.isEmpty()) {
            // Si hay tareas, se asigna el adaptador y se muestra el RecyclerView
            recyclerView.setAdapter(new TareaAdapter(this, listaTareas));
            recyclerView.setVisibility(View.VISIBLE);
            mensaje.setVisibility(View.GONE);
        } else {
            // Si no hay tareas, se muestra el mensaje de vacío
            mensaje.setVisibility(View.VISIBLE);
            mensaje.setText(mensajeVacio);
            recyclerView.setVisibility(View.GONE);
        }
    }



    // Método para dirigir al usuario al menú de inicio
    public void Dirigir(View view) {
        Intent intent = new Intent(this, menu_inicio.class);
        startActivity(intent);
        finish();
    }
}
