package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.la_papelera_est_vac_a;
import static sv.edu.catolica.rememhub.R.string.papelera_vaciada;

import android.content.Intent;
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

        btnVaciarPapelera.setOnClickListener(v -> vaciarPapelera());
    }

    private void cargarTareasPapelera() {
        List<Tarea> listaTareasPapelera = tareaDataAccess.obtenerTareasPapelera();
        if (listaTareasPapelera != null && !listaTareasPapelera.isEmpty()) {
            if (adapter == null) {
                adapter = new TareaPapeleraAdapter(this, listaTareasPapelera);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.actualizarTareas(listaTareasPapelera); // Método para actualizar el listado
            }
        } else {
            if (adapter != null) {
                adapter.vaciarPapelera(); // Limpiar lista visual si ya existe
            }
            Toast.makeText(this, la_papelera_est_vac_a, Toast.LENGTH_SHORT).show();
        }
    }

    private void vaciarPapelera() {
        // Intenta vaciar la papelera y notifica cambios visuales
        List<Tarea> listaTareasPapelera = tareaDataAccess.obtenerTareasPapelera();
        if (listaTareasPapelera == null || listaTareasPapelera.isEmpty()) {
            Toast.makeText(this, la_papelera_est_vac_a, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            tareaDataAccess.eliminarTareasPapelera();
            cargarTareasPapelera(); // Recarga la lista después de vaciar la papelera
            Toast.makeText(this, papelera_vaciada, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_al_vaciar_papelera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareasPapelera();
    }
}
