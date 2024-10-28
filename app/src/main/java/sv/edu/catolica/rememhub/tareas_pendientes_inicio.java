package sv.edu.catolica.rememhub;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class tareas_pendientes_inicio extends AppCompatActivity {

    private RecyclerView recyclerViewEstaSemana;
    private RecyclerView recyclerViewSiguienteSemana;
    private TareaAdapter tareaAdapterEstaSemana;
    private TareaAdapter tareaAdapterSiguienteSemana;
    private List<Tarea> listaTareasEstaSemana;
    private List<Tarea> listaTareasSiguienteSemana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tareas_pendientes_inicio);

        recyclerViewEstaSemana = findViewById(R.id.recyclerViewEstaSemana);
        recyclerViewSiguienteSemana = findViewById(R.id.recyclerViewSiguienteSemana);

        // Configurar LayoutManager
        recyclerViewEstaSemana.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSiguienteSemana.setLayoutManager(new LinearLayoutManager(this));

        listaTareasEstaSemana = new ArrayList<>();
        listaTareasSiguienteSemana = new ArrayList<>();

        listaTareasEstaSemana.add(new Tarea("Tarea 1", "Categoría 1", "01, 10, 2024", false));
        listaTareasSiguienteSemana.add(new Tarea("Tarea 2", "Categoría 2", "08, 10, 2024", false));

        tareaAdapterEstaSemana = new TareaAdapter(this, listaTareasEstaSemana);
        tareaAdapterSiguienteSemana = new TareaAdapter(this, listaTareasSiguienteSemana);

        recyclerViewEstaSemana.setAdapter(tareaAdapterEstaSemana);
        recyclerViewSiguienteSemana.setAdapter(tareaAdapterSiguienteSemana);
    }

    public void Dirigir(View view) {

    }
}
