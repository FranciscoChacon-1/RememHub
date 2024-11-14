package sv.edu.catolica.rememhub;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class activity_historial extends AppCompatActivity {

    private Button dateButton;
    private RecyclerView recyclerViewHistorial;
    private TareaAdapter tareaAdapter;
    private RememhubBD dbHelper;
    private List<Tarea> listaTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        dbHelper = new RememhubBD(this);
        dateButton = findViewById(R.id.dateButton);
        EditText searchField = findViewById(R.id.search);
        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);

        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();

        dateButton.setOnClickListener(v -> openDatePicker());
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadTasks() {
        listaTareas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT T.id, T.titulo, T.fecha_cumplimiento, T.estado, C.nombre AS categoria, " +
                "T.hora_cumplimiento, T.hora_recordatorio " +
                "FROM Tareas T LEFT JOIN Categorias C ON T.categoria_id = C.id " +
                "WHERE T.papelera = 0", null); // Solo carga tareas que no están en la papelera

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                int estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String horaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("hora_cumplimiento"));
                String horaRecordatorio = cursor.getString(cursor.getColumnIndexOrThrow("hora_recordatorio"));

                Tarea tarea = new Tarea(id, titulo, fechaCumplimiento, categoria, fechaCumplimiento, horaCumplimiento, horaRecordatorio);
                tarea.setCompletada(estado == 1); // Marca el checkbox si estado es 1
                listaTareas.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        tareaAdapter = new TareaAdapter(this, listaTareas);
        recyclerViewHistorial.setAdapter(tareaAdapter);
    }


    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateButton(calendar);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", (dialog, which) -> {
            if (which == DatePickerDialog.BUTTON_NEGATIVE) {
                dateButton.setText(R.string.fecha);
                loadTasks();
            }
        });
        datePicker.show();
    }

    private void updateDateButton(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()); // Cambiamos el formato a 'yyyy-M-d' para evitar ceros a la izquierda
        String formattedDate = dateFormat.format(calendar.getTime());
        dateButton.setText(formattedDate);
        filterTasksByDate(formattedDate);
    }




    // Para filtrar por nombre
    private void filterTasks(String query) {
        List<Tarea> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            loadTasks();  // Cargar todas las tareas si el campo de búsqueda está vacío
        } else {
            for (Tarea tarea : listaTareas) {
                if (tarea.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(tarea);
                }
            }
            tareaAdapter.updateData(filteredList);
        }
    }

    // Para filtrar por Fecha
    private void filterTasksByDate(String selectedDateString) {
        // Restauramos la lista completa de tareas antes de aplicar el filtro
        loadTasks();

        List<Tarea> filteredList = new ArrayList<>();
        Log.d("FilterTasks", "Fecha seleccionada para filtrar: " + selectedDateString);

        for (Tarea tarea : listaTareas) {
            Log.d("FilterTasks", "Fecha de la tarea: " + tarea.getFechaCumplimientoString());

            // Comparamos usando el formato 'yyyy-M-d' para evitar diferencias en ceros a la izquierda
            if (tarea.getFechaCumplimientoString() != null &&
                    tarea.getFechaCumplimientoString().equals(selectedDateString)) {
                filteredList.add(tarea);
            }
        }

        tareaAdapter.updateData(filteredList);
    }
}
