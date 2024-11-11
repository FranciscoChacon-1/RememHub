package sv.edu.catolica.rememhub;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Calendar selectedDate;
    private EditText searchField;
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
        searchField = findViewById(R.id.search);
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
        Cursor cursor = db.rawQuery("SELECT T.id, T.titulo, T.fecha_cumplimiento, C.nombre AS categoria FROM Tareas T LEFT JOIN Categorias C ON T.categoria_id = C.id", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                listaTareas.add(new Tarea(id, titulo, fechaCumplimiento, categoria));
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
                    selectedDate = calendar;
                    updateDateButton(calendar);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", (dialog, which) -> {
            if (which == DatePickerDialog.BUTTON_NEGATIVE) {
                selectedDate = null;
                dateButton.setText(R.string.fecha);
                loadTasks();
            }
        });
        datePicker.show();
    }

    private void updateDateButton(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateButton.setText(dateFormat.format(calendar.getTime()));
        filterTasksByDate(calendar);
    }

    private void filterTasks(String query) {
        List<Tarea> filteredList = new ArrayList<>();
        for (Tarea tarea : listaTareas) {
            if (tarea.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(tarea);
            }
        }
        tareaAdapter.updateData(filteredList);
    }

    private void filterTasksByDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String selectedDateString = dateFormat.format(date.getTime());
        List<Tarea> filteredList = new ArrayList<>();
        for (Tarea tarea : listaTareas) {
            if (tarea.getFechaCumplimiento() != null && tarea.getFechaCumplimiento().equals(selectedDateString)) {
                filteredList.add(tarea);
            }
        }
        tareaAdapter.updateData(filteredList);
    }
}
