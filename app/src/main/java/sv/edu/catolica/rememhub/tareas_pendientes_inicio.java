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
    private TextView mensajeProximas;
    private List<Tarea> listaTareasProximas;
    private RememhubBD dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_pendientes_inicio);

        inicializarVistas();
        dbHelper = new RememhubBD(this);

        new CargarTareasAsync().execute();
    }

    // Inicializa los RecyclerView y TextView
    private void inicializarVistas() {
        recyclerViewProximas = configurarRecyclerView(R.id.recyclerViewProximas);

        mensajeProximas = findViewById(R.id.mensajeProximas);
    }

    // Configura un RecyclerView con un LinearLayoutManager
    private RecyclerView configurarRecyclerView(int recyclerViewId) {
        RecyclerView recyclerView = findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        return recyclerView;
    }

    // Clase AsyncTask para cargar las tareas en segundo plano
    private class CargarTareasAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            TareaDataAccess tareaDataAccess = new TareaDataAccess(tareas_pendientes_inicio.this);
            listaTareasProximas = tareaDataAccess.obtenerTareasProximas();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            actualizarVistas();
        }
    }

    // Actualiza las vistas de los RecyclerView y los mensajes
    private void actualizarVistas() {
        actualizarVista(recyclerViewProximas, mensajeProximas, listaTareasProximas, R.string.mensajeProximas);

    }

    // Método general para actualizar un RecyclerView con una lista de tareas o un mensaje
    private void actualizarVista(RecyclerView recyclerView, TextView mensaje, List<Tarea> listaTareas, int mensajeVacioResId) {
        if (listaTareas != null && !listaTareas.isEmpty()) {
            recyclerView.setAdapter(new TareaAdapter(this, listaTareas));
            recyclerView.setVisibility(View.VISIBLE);
            mensaje.setVisibility(View.GONE);
        } else {
            mensaje.setVisibility(View.VISIBLE);
            mensaje.setText(mensajeVacioResId);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void Dirigir(View view) {
        startActivity(new Intent(this, menu_inicio.class));
        finish();
    }
}

