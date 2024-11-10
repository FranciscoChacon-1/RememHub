package sv.edu.catolica.rememhub;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sv.edu.catolica.rememhub.db.DbCategorias;
import sv.edu.catolica.rememhub.db.DbTareas;

public class VerTareasxCategorias extends AppCompatActivity {
    Spinner spinnercat;
    RecyclerView lista;
    TareaAdapter tareaAdapter; // Adaptador para el RecyclerView
    List<Tarea> listaTareas; // Lista de tareas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_tareasx_categorias);

        spinnercat = findViewById(R.id.spinner2);
        lista = findViewById(R.id.VerTarea);
        lista.setLayoutManager(new LinearLayoutManager(this));

        // Cargar categorías en el spinner
        List<Categorias> listCat = llenarCategorias();
        ArrayAdapter<Categorias> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCat);
        spinnercat.setAdapter(arrayAdapter);

        // Cargar tareas al seleccionar una categoría
        spinnercat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Categorias categoriaSeleccionada = (Categorias) adapterView.getItemAtPosition(position);
                cargarTareas(categoriaSeleccionada.getId()); // Cargar tareas de la categoría seleccionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No hacer nada
            }
        });
    }

    private void cargarTareas(int categoriaId) {
        // Aquí debes implementar la lógica para cargar las tareas de la base de datos según la categoría
        DbTareas dbTareas = new DbTareas(this);
        listaTareas = dbTareas.obtenerTareasPorCategoria(categoriaId); // Método que debes implementar en DbTareas

        // Configurar el adaptador con la lista de tareas
        tareaAdapter = new TareaAdapter(this, listaTareas);
        lista.setAdapter(tareaAdapter);
    }

    private List<Categorias> llenarCategorias() {
        List<Categorias> listCat = new ArrayList<>();
        DbCategorias dbCategorias = new DbCategorias(this);
        Cursor cursor = dbCategorias.selecionarcategoria();

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int idIndex = cursor.getColumnIndexOrThrow("id");
                        int nombreIndex = cursor.getColumnIndexOrThrow("nombre");

                        Categorias cat = new Categorias();
                        cat.setId(cursor.getInt(idIndex));
                        cat.setNombre(cursor.getString(nombreIndex));
                        listCat.add(cat);
                    } while (cursor.moveToNext());
                }
            } catch (IllegalArgumentException e) {
                Log.e("DbError", "Error: " + e.getMessage());
            } finally {
                cursor.close(); // Asegúrate de cerrar el cursor
            }
        }
        dbCategorias.close();
        return listCat;
    }
}