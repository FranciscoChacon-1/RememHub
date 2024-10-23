package sv.edu.catolica.rememhub;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Calendar;

public class detalles_de_tarea extends AppCompatActivity {

    private TextView tvFecha, tvHora;
    private Spinner spTarea, spCategoria; // Cambiado listCategoria a spCategoria
    private EditText etTituloTarea, etDescripcion;
    private CheckBox checkBoxCompletada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles_de_tarea);

        // Asignar los componentes de la vista a las variables
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        spTarea = findViewById(R.id.spTarea);
        spCategoria = findViewById(R.id.spCategoria); // Cambiado a Spinner
        etTituloTarea = findViewById(R.id.etTituloTarea);
        etDescripcion = findViewById(R.id.etDescripcion);
        checkBoxCompletada = findViewById(R.id.checkBoxCompletada);

        //Ejemplo Seleccionar tarea
        ArrayList<String> aTareas = new ArrayList<>();
        aTareas.add("Tarea 1");
        aTareas.add("Tarea 2");
        aTareas.add("Tarea 3");

        //Configurar tareas
        ArrayAdapter<String> adapterTareas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, aTareas);
        adapterTareas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTarea.setAdapter(adapterTareas);

        // Crear una lista de categorías con algunos elementos de ejemplo
        ArrayList<String> aCategorias = new ArrayList<>();
        aCategorias.add("Categoría 1");
        aCategorias.add("Categoría 2");
        aCategorias.add("Categoría 3");

        // Configurar el adaptador con la lista
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, aCategorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias); // Asignar adaptador al Spinner de categorías

        // Configurar listeners para seleccionar fecha y hora
        tvFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tvHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Configurar los márgenes con WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDatePickerDialog() {
        // Obtener la fecha actual
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear un DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // Actualizar el texto del TextView con la fecha seleccionada
                    tvFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                },
                year, month, day
        );

        // Mostrar el diálogo
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // Obtener la hora actual
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Crear un TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    // Actualizar el texto del TextView con la hora seleccionada
                    tvHora.setText(String.format("%02d:%02d", hourOfDay, minute1));
                },
                hour, minute, true
        );

        // Mostrar el diálogo
        timePickerDialog.show();
    }
}
