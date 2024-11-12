package sv.edu.catolica.rememhub;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import android.view.View;
import android.widget.Button;

public class PapeleraActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TareaPapeleraAdapter adapter;
    private TareaDataAccess tareaDataAccess;
    private Button btnVaciarPapelera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);

        tareaDataAccess = new TareaDataAccess(this);
        recyclerView = findViewById(R.id.recyclerViewPapelera);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnVaciarPapelera = findViewById(R.id.btnVaciarPapelera);

        cargarTareasPapelera();

        btnVaciarPapelera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarPapelera();
            }
        });
    }

    private void cargarTareasPapelera() {
        List<Tarea> listaTareasPapelera = tareaDataAccess.obtenerTareasPapelera();
        if (listaTareasPapelera != null && !listaTareasPapelera.isEmpty()) {
            adapter = new TareaPapeleraAdapter(this, listaTareasPapelera);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "La papelera está vacía", Toast.LENGTH_SHORT).show();
        }
    }

    private void vaciarPapelera() {
        // Llama al método en TareaDataAccess para eliminar todas las tareas en papelera y sus días de recordatorio
        tareaDataAccess.eliminarTareasPapelera();
        cargarTareasPapelera(); // Recarga la lista después de vaciar la papelera
        Toast.makeText(this, "Papelera vaciada", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareasPapelera();
    }
}
