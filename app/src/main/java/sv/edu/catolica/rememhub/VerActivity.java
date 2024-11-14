package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.categoria_eliminada;

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
                builder.setMessage("Desea Eliminar esta Categoria")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dbCategorias.ElimiarCategoria(id)) {
                                    Toast.makeText(VerActivity.this, categoria_eliminada, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(VerActivity.this, Categoria_activity.class);
                                    intent.putExtra("ID", id);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

    }


}