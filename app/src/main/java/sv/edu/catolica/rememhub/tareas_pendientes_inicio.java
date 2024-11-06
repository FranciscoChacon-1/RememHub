package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_pendientes_inicio); // Asegúrate de que este layout tenga los RecyclerView

        // Inicializar los RecyclerViews
        recyclerViewEstaSemana = findViewById(R.id.recyclerViewEstaSemana);
        recyclerViewSiguienteSemana = findViewById(R.id.recyclerViewSiguienteSemana);

        // Configurar el LayoutManager para los RecyclerViews
        recyclerViewEstaSemana.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSiguienteSemana.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el acceso a la base de datos
        dbHelper = new RememhubBD(this);

        // Obtener las tareas de la base de datos
        TareaDataAccess tareaDataAccess = new TareaDataAccess(this);

        // Cargar tareas para esta semana y la siguiente semana
        listaTareasEstaSemana = tareaDataAccess.obtenerTareasSemana(true);  // Tareas de esta semana
        listaTareasSiguienteSemana = tareaDataAccess.obtenerTareasSemana(false);  // Tareas de la siguiente semana

        // Verificar si las listas de tareas están vacías
        if (listaTareasEstaSemana.isEmpty() && listaTareasSiguienteSemana.isEmpty()) {
            // Si no hay tareas, muestra un mensaje
            Toast.makeText(this, "No hay tareas disponibles", Toast.LENGTH_SHORT).show();
        }

        // Crear los adaptadores y asignarlos a los RecyclerViews
        tareaAdapterEstaSemana = new TareaAdapter(this, listaTareasEstaSemana);
        tareaAdapterSiguienteSemana = new TareaAdapter(this, listaTareasSiguienteSemana);

        recyclerViewEstaSemana.setAdapter(tareaAdapterEstaSemana);
        recyclerViewSiguienteSemana.setAdapter(tareaAdapterSiguienteSemana);
    }

    // Si más adelante quieres refrescar la lista de tareas (por ejemplo, después de marcar una tarea como completada)
    public void actualizarListaTareas(List<Tarea> nuevasTareas) {
        tareaAdapterEstaSemana = new TareaAdapter(this, nuevasTareas); // Actualiza para esta semana o según sea necesario
        recyclerViewEstaSemana.setAdapter(tareaAdapterEstaSemana);
    }

    public void Dirigir(View view) {
        Intent intent;
        intent = new Intent(this, menu_inicio.class);
        startActivity(intent);    }
}
