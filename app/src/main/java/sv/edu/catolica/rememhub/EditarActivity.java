package sv.edu.catolica.rememhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sv.edu.catolica.rememhub.db.DbCategorias;


public class EditarActivity extends AppCompatActivity {
    EditText txtNombre;
    Button btnguardarcat;
    boolean correcto = false;
    FloatingActionButton fabEditar,fabEliminar;

    Categorias categorias;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cat);
        txtNombre = findViewById(R.id.editTxtConfiguracion);
        btnguardarcat = findViewById(R.id.btnguardarCat);
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabElminar);
        fabEditar.setVisibility(View.INVISIBLE);
        fabEliminar.setVisibility(View.INVISIBLE);


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



        DbCategorias dbCategorias = new DbCategorias(EditarActivity.this);
        categorias = dbCategorias.verCategorias(id);

        if (categorias != null) {
            txtNombre.setText(categorias.getNombre());

        }

        btnguardarcat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!txtNombre.getText().toString().equals("")){
                    correcto = dbCategorias.editarCategoria(id, txtNombre.getText().toString());
                    if (correcto){
                        Toast.makeText(EditarActivity.this, "Categoria Actualizada", Toast.LENGTH_SHORT).show();
                        verRegistro();
                        // Regresar a MainActivity
                        Intent intent = new Intent(EditarActivity.this, Categoria_activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent); // Inicia Categoria
                        finish(); // Cierra EditarActivity

                    }else{
                        Toast.makeText(EditarActivity.this, R.string.error_al_actualizar, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditarActivity.this, "El campo no puede estar vacio", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
    private void verRegistro() {
        Intent intent = new Intent(this, VerActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);


    }
}
