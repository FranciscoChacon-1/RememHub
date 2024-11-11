package sv.edu.catolica.rememhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sv.edu.catolica.rememhub.db.DbCategorias;

public class MainActivity2 extends AppCompatActivity {
    private EditText e1,txtNombre;
    private Button btnguardarcat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtNombre=findViewById(R.id.editText);
        btnguardarcat=findViewById(R.id.btnguardarCat);

        btnguardarcat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Obtener el texto del campo de entrada
                String nombreCategoria = txtNombre.getText().toString().trim();

                // Validar si el campo está vacío o contiene solo espacios
                if (nombreCategoria.isEmpty()) {
                    // Mostrar un mensaje de error si el campo está vacío
                    Toast.makeText(MainActivity2.this, "El nombre de la categoría no puede estar vacío", Toast.LENGTH_SHORT).show();
                } else {
                    // Si el campo no está vacío, continuar con el proceso de guardar la categoría
                    DbCategorias dbCategorias = new DbCategorias(MainActivity2.this);
                    long id = dbCategorias.insertarCategoria(nombreCategoria);

                    if (id > 0) {
                        Toast.makeText(MainActivity2.this, "Categoría Agregada", Toast.LENGTH_SHORT).show();
                        limpiar(); // Método para limpiar el campo de entrada
                    } else {
                        Toast.makeText(MainActivity2.this, "Error al guardar la Categoría", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void limpiar() {
        txtNombre.setText("");
    }
}