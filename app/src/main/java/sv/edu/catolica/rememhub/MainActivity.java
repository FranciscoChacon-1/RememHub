package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import sv.edu.catolica.rememhub.adaptadores.ListaCategoriasAdapter;
import sv.edu.catolica.rememhub.db.DbCategorias;

public class MainActivity extends AppCompatActivity {
    RecyclerView listaCategoria;
    ArrayList<Categorias> listArrayCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_categorias), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listaCategoria=findViewById(R.id.ListaCategoria);
        listaCategoria.setLayoutManager(new LinearLayoutManager(this));

        DbCategorias dbCategorias = new DbCategorias(MainActivity.this);
        listArrayCategorias = new ArrayList<Categorias>();
        ListaCategoriasAdapter adapter = new ListaCategoriasAdapter(dbCategorias.mostrarContactos());
        listaCategoria.setAdapter(adapter);




    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pricipal, menu);
        return true;


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == id.menus_nuevo) {
            nuevoRegistro(); // Asegúrate de que este método esté definido
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void nuevoRegistro() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }


}


