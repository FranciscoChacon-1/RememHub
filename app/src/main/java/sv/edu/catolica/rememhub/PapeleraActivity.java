package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PapeleraActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPapelera;
    private TareaAdapter tareaAdapterPapelera;
    private List<Tarea> listaTareasPapelera;
    private RememhubBD dbHelper;
    private TareaDataAccess tareaDataAccess;
    private TextView textViewTarea;
    private String tareaNombre;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);

        // Inicializar el TextView
        textViewTarea = findViewById(R.id.textViewTareaPapelera);

        // Recibir el nombre de la tarea desde el Intent (en caso de que quieras mostrar detalles)
        tareaNombre = getIntent().getStringExtra("tarea_nombre");

        // Inicializar el acceso a la base de datos
        dbHelper = new RememhubBD(this);
        tareaDataAccess = new TareaDataAccess(this);

        // Inicializar el RecyclerView
        recyclerViewPapelera = findViewById(R.id.recyclerViewPapelera);
        recyclerViewPapelera.setLayoutManager(new LinearLayoutManager(this));

        // Obtener las tareas eliminadas de la base de datos
        listaTareasPapelera = tareaDataAccess.obtenerTareasEliminadas();

// Crear el adaptador y asignarlo al RecyclerView
        tareaAdapterPapelera = new TareaAdapter(this, listaTareasPapelera); // No se necesita pasar 'true' o 'false' aquí
        recyclerViewPapelera.setAdapter(tareaAdapterPapelera);


        // Si hay un nombre de tarea, mostrar detalles (si es necesario)
        if (tareaNombre != null) {
            Tarea tarea = obtenerTareaPorNombre(tareaNombre);
            if (tarea != null) {
                textViewTarea.setText(tarea.getNombre() + "\n" + tarea.getCategoria() + "\n" + tarea.getFecha());
            }
        }

        // Botón para vaciar la papelera
        Button btnVaciarPapelera = findViewById(R.id.btnVaciarPapelera);
        btnVaciarPapelera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarPapelera();
            }
        });
    }

    // Método para vaciar la papelera
    private void vaciarPapelera() {
        // Lógica para vaciar la papelera
        for (Tarea tarea : listaTareasPapelera) {
            tareaDataAccess.eliminarTarea(tarea);  // Método para eliminar una tarea de la base de datos
        }

        // Actualizar la interfaz de usuario
        listaTareasPapelera.clear();
        tareaAdapterPapelera.notifyDataSetChanged();

        Toast.makeText(this, "Papelera vacía", Toast.LENGTH_SHORT).show();
    }

    // Método para obtener la tarea por nombre
    private Tarea obtenerTareaPorNombre(String nombre) {
        return tareaDataAccess.obtenerTareaPorNombre(nombre);
    }
}
