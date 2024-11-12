package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            TareaDataAccess tareaDataAccess = new TareaDataAccess(tareas_pendientes_inicio.this);
            listaTareasProximas = tareaDataAccess.obtenerTareasProximas();  // Tareas próximas
            listaTareasExpiradas = tareaDataAccess.obtenerTareasExpiradas();  // Tareas expiradas
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            actualizarVistas();
        }
    }

    // Método para actualizar las vistas de los RecyclerView y los mensajes
    private void actualizarVistas() {
        actualizarVista(recyclerViewProximas, mensajeProximas, listaTareasProximas, getString(R.string.mensajeProximas));
        actualizarVista(recyclerViewExpiradas, mensajeExpiradas, listaTareasExpiradas, getString(R.string.mensajeExpiradas));
    }

    // Método general para actualizar las vistas de cada RecyclerView
    private void actualizarVista(RecyclerView recyclerView, TextView mensaje, List<Tarea> listaTareas, String mensajeVacio) {
        if (listaTareas != null && !listaTareas.isEmpty()) {
            recyclerView.setAdapter(new TareaAdapter(this, listaTareas));
            recyclerView.setVisibility(View.VISIBLE);
            mensaje.setVisibility(View.GONE);
        } else {
            mensaje.setVisibility(View.VISIBLE);
            mensaje.setText(mensajeVacio);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void Dirigir(View view) {
        Intent intent = new Intent(this, menu_inicio.class);
        startActivity(intent);
        finish();
    }
}

