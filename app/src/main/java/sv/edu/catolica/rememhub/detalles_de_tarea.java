package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class detalles_de_tarea extends AppCompatActivity {

    private TextView tvFecha, tvHora;
    private Spinner spTarea, spCategoria;
    private EditText etTituloTarea, etDescripcion;
    private CheckBox checkBoxCompletada;
    private Button btnEditar, btnEliminar;

    private RememhubBD dbHelper;
    private int selectedTareaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_de_tarea);

        // Asignar los componentes de la vista a las variables
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        spTarea = findViewById(R.id.spTarea);
        spCategoria = findViewById(R.id.spCategoria);
        etTituloTarea = findViewById(R.id.etTituloTarea);
        etDescripcion = findViewById(R.id.etDescripcion);
        checkBoxCompletada = findViewById(R.id.checkBoxCompletada);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        dbHelper = new RememhubBD(this);

        // Cargar tareas y categorías en los spinners
        cargarTareas();
        cargarCategorias();

        // Configurar listeners para seleccionar fecha y hora
        tvFecha.setOnClickListener(v -> showDatePickerDialog());
        tvHora.setOnClickListener(v -> showTimePickerDialog());

        // Configurar botones
        btnEditar.setOnClickListener(v -> editarTarea());
        btnEliminar.setOnClickListener(v -> eliminarTarea());

        // Listener para cargar los datos de la tarea seleccionada
        spTarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarDatosTarea();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cargarTareas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> tareasList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, titulo FROM Tareas", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                tareasList.add(titulo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapterTareas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tareasList);
        adapterTareas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTarea.setAdapter(adapterTareas);
    }

    private void cargarCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> categoriasList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM Categorias", null);

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(1);
                categoriasList.add(nombre);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriasList);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);
    }

    private void cargarDatosTarea() {
        // Obtener la tarea seleccionada
        selectedTareaId = spTarea.getSelectedItemPosition() + 1; // Suponiendo que el ID coincide con la posición
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, descripcion, categoria_id, fecha_cumplimiento, hora_cumplimiento, estado FROM Tareas WHERE id = ?", new String[]{String.valueOf(selectedTareaId)});

        if (cursor.moveToFirst()) {
            etTituloTarea.setText(cursor.getString(0));
            etDescripcion.setText(cursor.getString(1));
            spCategoria.setSelection(cursor.getInt(2) - 1); // Ajuste de índice
            tvFecha.setText(cursor.getString(3));
            tvHora.setText(cursor.getString(4));
            checkBoxCompletada.setChecked(cursor.getInt(5) == 1);
        }
        cursor.close();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                //(view, year1, month1, dayOfMonth) -> tvFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                (view, year1, month1, dayOfMonth) -> tvFecha.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth),
                                                                                        //'2024-10-03'
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> tvHora.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void editarTarea() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String titulo = etTituloTarea.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        int categoriaId = spCategoria.getSelectedItemPosition() + 1;
        String fecha = tvFecha.getText().toString();
        String hora = tvHora.getText().toString();
        int estado = checkBoxCompletada.isChecked() ? 1 : 0;

        db.execSQL("UPDATE Tareas SET titulo = ?, descripcion = ?, categoria_id = ?, fecha_cumplimiento = ?, hora_cumplimiento = ?, estado = ? WHERE id = ?",
                new Object[]{titulo, descripcion, categoriaId, fecha, hora, estado, selectedTareaId});
        Toast.makeText(this, "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void eliminarTarea() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM Tareas WHERE id = ?", new Object[]{selectedTareaId});
        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
        cargarTareas(); // Recargar tareas para actualizar la lista
    }
}
