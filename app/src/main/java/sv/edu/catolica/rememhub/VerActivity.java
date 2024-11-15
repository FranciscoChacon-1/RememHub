package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.categoria_eliminada;
import static sv.edu.catolica.rememhub.R.string.desea_eliminar_esta_categoria;

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

import sv.edu.catolica.rememhub.db.DbCategorias;

public class VerActivity extends AppCompatActivity {
    private EditText txtNombre;
    Button btnguardarcat;
    FloatingActionButton fabEditar,fabEliminar;
    boolean correcto = false;

    Categorias categorias;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_cat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.actvityVer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtNombre = findViewById(R.id.editTxtConfiguracion);
        btnguardarcat = findViewById(R.id.btnguardarCat);
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabElminar);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = 0; // Usar un valor por defecto como 0 en lugar de Integer.parseInt(null)
            } else {
                id = extras.getInt("ID", 0);
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        DbCategorias dbCategorias = new DbCategorias(VerActivity.this);
        categorias = dbCategorias.verCategorias(id);

        if (categorias != null) {
            txtNombre.setText(categorias.getNombre());
            btnguardarcat.setVisibility(View.INVISIBLE);
            txtNombre.setInputType(InputType.TYPE_NULL);
        }

        fabEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerActivity.this, EditarActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
                finish();
            }
        });
        fabEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerActivity.this);
                builder.setMessage(desea_eliminar_esta_categoria)
                        .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Intentar eliminar la categoría
                                if (dbCategorias.ElimiarCategoria(id)) {
                                    Toast.makeText(VerActivity.this, categoria_eliminada, Toast.LENGTH_SHORT).show();

                                    // Iniciar la nueva actividad y actualizarla
                                    Intent intent = new Intent(VerActivity.this, Categoria_activity.class); // Cambia CategoriaActivity por el nombre de tu actividad de destino
                                    intent.putExtra("ID", id); // Si necesitas pasar el ID, puedes hacerlo aquí
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpia la pila de actividades
                                    startActivity(intent); // Inicia la nueva actividad
                                    finish(); // Cierra VerActivity
                                } else {
                                    Toast.makeText(VerActivity.this, "Error al eliminar la categoría", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // No hacer nada, solo cerrar el diálogo
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

    }


}