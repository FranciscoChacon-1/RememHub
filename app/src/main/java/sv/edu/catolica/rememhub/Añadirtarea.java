package sv.edu.catolica.rememhub;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sv.edu.catolica.rememhub.db.DbCategorias;
import sv.edu.catolica.rememhub.db.DbTareas;

public class Añadirtarea  extends AppCompatActivity{
    EditText et1, et2,et3, txtTitulo, txtDescripcion;
    Spinner SpCategorias, spDia;
    Context context;
    private String[] opciones = {"No repetir", "Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.anadir_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_tarea), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        et3=findViewById(R.id.et3);
        txtTitulo=findViewById(R.id.txtTitulo);
        txtDescripcion=findViewById(R.id.txtDescripcion);
        SpCategorias = findViewById(R.id.spinner);
        spDia = findViewById(R.id.spDias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDia.setAdapter(adapter);
        Button btnCancelar = findViewById(R.id.btncancelar);
        Button btnGuardar = findViewById(R.id.btnguardar);

        List<Categorias> listCat = llenarCategorias();
        ArrayAdapter<Categorias> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCat);
        SpCategorias.setAdapter(arrayAdapter);

        SpCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categorias categoriaSeleccionada = (Categorias) parent.getItemAtPosition(position);
                int categoriaId = categoriaSeleccionada.getId(); // Obtén el ID de la categoría seleccionada
                // Aquí puedes usar categoriaId para guardarlo en la base de datos o donde necesites
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });





        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (opciones[position].equals("Otro...")) {
                    mostrarDialogoDias();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    public void Guardar(View view) {
        try {
            DbTareas dbTareas = new DbTareas(Añadirtarea.this);
            Categorias categoriaSeleccionada = (Categorias) SpCategorias.getSelectedItem();

            // Validar que la categoría seleccionada no sea nula
            if (categoriaSeleccionada == null) {
                Toast.makeText(this, "No se ha seleccionado una categoría válida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el ID de la categoría seleccionada
            int categoriaId = categoriaSeleccionada.getId();

            // Validar que los campos no estén vacíos
            String titulo = txtTitulo.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String fechaCumplimiento = et1.getText().toString();
            String horaCumplimiento = et2.getText().toString();
            String horaRecordatorio = et3.getText().toString();

            if (titulo.isEmpty() || descripcion.isEmpty() || fechaCumplimiento.isEmpty() || horaCumplimiento.isEmpty() || horaRecordatorio.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insertar la tarea en la base de datos
            long id = dbTareas.insertarTarea(titulo, descripcion, categoriaId, fechaCumplimiento, horaCumplimiento, horaRecordatorio);

            if (id > 0) {
                Toast.makeText(Añadirtarea.this, "Tarea guardada con ID: " + id, Toast.LENGTH_SHORT).show();
                clearFields(); // Limpiar los campos después de guardar
            } else {
                Toast.makeText(Añadirtarea.this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(Añadirtarea.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Para ver detalles en la consola
        }
    }

    private void clearFields() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        et1.setText("");
        et2.setText("");
        et3.setText("");
        SpCategorias.setSelection(0); // Restablecer el spinner a la primera opción
    }

    public void mostrarCalendario(View v){
        showDatePickerDialog();
    }
    public void mostrarHorario(View V){
        TimePickerDialog d=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                et2.setText(i+":"+i1);

            }
        } ,10,30,true);
        d.show();
    }
    public void mostrarRecordatorio(View V){
        TimePickerDialog d=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                et3.setText(i+":"+i1);

            }
        } ,10,30,true);
        d.show();
    }

    private List<Categorias> llenarCategorias() {
        List<Categorias> listCat = new ArrayList<>();
        DbCategorias dbCategorias = new DbCategorias(Añadirtarea.this);
        Cursor cursor = dbCategorias.selecionarcategoria();

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int idIndex = cursor.getColumnIndexOrThrow("id");
                        int nombreIndex = cursor.getColumnIndexOrThrow("nombre");

                        Categorias cat = new Categorias();
                        cat.setId(cursor.getInt(idIndex));
                        cat.setNombre(cursor.getString(nombreIndex));
                        listCat.add(cat);
                    } while (cursor.moveToNext());
                }
            } catch (IllegalArgumentException e) {
                Log.e("DbError", "Error: " + e.getMessage());
            } finally {
                cursor.close(); // Asegúrate de cerrar el cursor
            }
        }
        dbCategorias.close();
        return listCat;
    }

    private void mostrarDialogoDias() {
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        boolean[] diasSeleccionados = new boolean[diasSemana.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona los días");
        builder.setMultiChoiceItems(diasSemana, diasSeleccionados, (dialog, which, isChecked) -> {
            // Maneja la selección de días
            diasSeleccionados[which] = isChecked;
        });

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Mostrar los días seleccionados
            StringBuilder dias = new StringBuilder();
            for (int i = 0; i < diasSemana.length; i++) {
                if (diasSeleccionados[i]) {
                    dias.append(diasSemana[i]).append(" ");
                }
            }
            Toast.makeText(Añadirtarea.this, "Días seleccionados: " + dias.toString(), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void Cancelar(View view) {
        clearFields();
    }
    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the EditText with the selected date
                    et1.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }




}
