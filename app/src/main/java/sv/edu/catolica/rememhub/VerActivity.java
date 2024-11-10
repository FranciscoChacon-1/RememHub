package sv.edu.catolica.rememhub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VerActivity extends AppCompatActivity {
    private EditText txtNombre;
    Button btnguardarcat;
    FloatingActionButton fabEditar, fabEliminar;
    boolean correcto = false;

    Categorias categorias;
    int id = 0;
    RememhubBD db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.actvityVer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombre = findViewById(R.id.editTxtConfiguracion);
        btnguardarcat = findViewById(R.id.btnguardarCat);
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabElminar);

        // Inicializar la base de datos RememhubBD
        db = new RememhubBD(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = 0;
            } else {
                id = extras.getInt("ID", 0);
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        // Obtener la categoría directamente de RememhubBD
        categorias = obtenerCategoria(id);

        if (categorias != null) {
            txtNombre.setText(categorias.getNombre());
            btnguardarcat.setVisibility(View.INVISIBLE);
            txtNombre.setInputType(InputType.TYPE_NULL);
        }

        fabEditar.setOnClickListener(view -> {
            Intent intent = new Intent(VerActivity.this, EditarActivity.class);
            intent.putExtra("ID", id);
            startActivity(intent);
        });

        fabEliminar.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VerActivity.this);
            builder.setMessage("Desea Eliminar esta Categoria")
                    .setPositiveButton("Si", (dialogInterface, i) -> {
                        if (eliminarCategoria(id)) {
                            Toast.makeText(VerActivity.this, "Categoria Eliminada", Toast.LENGTH_SHORT).show();
                            lista();
                        }
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> {
                    }).show();
        });
    }

    private Categorias obtenerCategoria(int id) {
        // Lógica para obtener la categoría usando RememhubBD
        // Aquí se debe consultar la base de datos y devolver una instancia de Categorias
        // Ejemplo de consulta:
        // SELECT * FROM Categorias WHERE id = ?
        // Nota: Implementa esta función según las necesidades específicas de tu aplicación.
        return null; // Reemplaza con la implementación adecuada
    }

    private boolean eliminarCategoria(int id) {
        // Lógica para eliminar la categoría usando RememhubBD
        // Ejemplo de eliminación:
        // DELETE FROM Categorias WHERE id = ?
        // Nota: Implementa esta función según las necesidades específicas de tu aplicación.
        return true; // Cambia este retorno según la lógica implementada
    }

    private void lista() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
