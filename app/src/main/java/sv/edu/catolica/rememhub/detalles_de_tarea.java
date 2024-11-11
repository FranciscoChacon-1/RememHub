package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import sv.edu.catolica.rememhub.db.DbTareas;

public class detalles_de_tarea extends AppCompatActivity {

    private EditText etTituloTarea, etDescripcion, etFecha, etHora, etHoraRecordatorio;
    private Spinner spTarea, spCategoria, spDias;
    private ImageButton btnFecha, btnHora, btnHoraRecordatorio;
    private Button btnCancelar, btnGuardar;
    private CheckBox checkBoxCompletada;
    private List<String> diasPersonalizados = new ArrayList<>();
    private String[] opciones = {"Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};

    private RememhubBD dbHelper;
    private int selectedTareaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_de_tarea);

        // Asignar los componentes de la vista a las variables

        etTituloTarea = findViewById(R.id.txtTitulo);
        etDescripcion = findViewById(R.id.txtDescripcion);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etHoraRecordatorio = findViewById(R.id.etHoraRecordatorio);
        spTarea = findViewById(R.id.spinnerTarea);
        spCategoria = findViewById(R.id.spinnerCategoria);
        spDias = findViewById(R.id.spDias);
        checkBoxCompletada = findViewById(R.id.checkBoxCompletada);
        btnFecha = findViewById(R.id.btnFecha);
        btnHora = findViewById(R.id.btnHora);
        btnHoraRecordatorio = findViewById(R.id.btnHoraRecordatorio);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGuardar = findViewById(R.id.btnGuardar);

        dbHelper = new RememhubBD(this);

        // Cargar las opciones en el spDias
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDias.setAdapter(adapterDias);

        // Cargar tareas y categorías en los spinners
        cargarTareas();
        cargarCategorias();

        // Configurar listeners para seleccionar fecha y hora
        btnFecha.setOnClickListener(v -> showDatePickerDialog());
        btnHora.setOnClickListener(v -> showTimePickerDialog());
        btnHoraRecordatorio.setOnClickListener(v -> showTimePickerDialogRecordatorio());

        // Configurar botones
        btnGuardar.setOnClickListener(v -> editarTarea());
        btnCancelar.setOnClickListener(v -> cancelarEdicion());

        // Listener para spDias para detectar selección de "Otros..."
        spDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (opciones[position].equals("Otro...")) {
                    showDiasPersonalizadosDialog();  // Mostrar diálogo de selección de días personalizados
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Listener para cargar los datos de la tarea seleccionada
        spTarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<Integer> tareasIds = (ArrayList<Integer>) spTarea.getTag();

                if (tareasIds != null && !tareasIds.isEmpty()) {
                    selectedTareaId = tareasIds.get(i); // Obtener el ID de la tarea seleccionada
                    cargarDatosTarea();
                } else {
                    selectedTareaId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTareaId = -1;
            }
        });
    }

    // Mostrar el diálogo de selección de días personalizados
    private void showDiasPersonalizadosDialog() {
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        boolean[] diasSeleccionados = new boolean[diasSemana.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona los días de recordatorio");

        builder.setMultiChoiceItems(diasSemana, diasSeleccionados, (dialog, which, isChecked) -> {
            if (isChecked) {
                diasPersonalizados.add(diasSemana[which]);
            } else {
                diasPersonalizados.remove(diasSemana[which]);
            }
        });

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            if (diasPersonalizados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un día", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void cargarTareas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> tareasList = new ArrayList<>();
        ArrayList<Integer> tareasIds = new ArrayList<>();

        // Consulta para obtener las tareas que no están en la papelera
        String query = "SELECT t.id, t.titulo FROM Tareas t LEFT JOIN Papelera p ON t.id = p.id WHERE p.id IS NULL";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                tareasList.add(titulo);
                tareasIds.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapterTareas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tareasList);
        adapterTareas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTarea.setAdapter(adapterTareas);

        spTarea.setTag(tareasIds);
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

    @SuppressLint("Range")
    private void cargarDatosTarea() {
        if (selectedTareaId != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            try (Cursor cursor = db.rawQuery(
                    "SELECT titulo, descripcion, categoria_id, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado " +
                            "FROM Tareas WHERE id = ?", new String[]{String.valueOf(selectedTareaId)})) {

                if (cursor.moveToFirst()) {
                    etTituloTarea.setText(cursor.getString(cursor.getColumnIndex("titulo")));
                    etDescripcion.setText(cursor.getString(cursor.getColumnIndex("descripcion")));
                    etFecha.setText(cursor.getString(cursor.getColumnIndex("fecha_cumplimiento")));
                    etHora.setText(cursor.getString(cursor.getColumnIndex("hora_cumplimiento")));
                    etHoraRecordatorio.setText(cursor.getString(cursor.getColumnIndex("hora_recordatorio")));

                    int categoriaId = cursor.getInt(cursor.getColumnIndex("categoria_id"));
                    ArrayAdapter<String> adapterCategorias = (ArrayAdapter<String>) spCategoria.getAdapter();
                    int categoriaPosition = categoriaId - 1;
                    if (categoriaPosition >= 0 && categoriaPosition < adapterCategorias.getCount()) {
                        spCategoria.setSelection(categoriaPosition);
                    }

                    int estado = cursor.getInt(cursor.getColumnIndex("estado"));
                    checkBoxCompletada.setChecked(estado == 1);
                }
                cursor.close();
            }
            db.close();
        }
    }

    private void editarTarea() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String categoriaSeleccionada = spCategoria.getSelectedItem().toString(); // Nombre de la categoría seleccionada
        int categoriaId = obtenerCategoriaIdPorNombre(categoriaSeleccionada); // Obtener ID de la categoría

        if (categoriaId == -1) {
            Toast.makeText(this, "Error al obtener la categoría seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("titulo", etTituloTarea.getText().toString());
        values.put("descripcion", etDescripcion.getText().toString());
        values.put("categoria_id", categoriaId);
        values.put("fecha_cumplimiento", etFecha.getText().toString());
        values.put("hora_cumplimiento", etHora.getText().toString());
        values.put("hora_recordatorio", etHoraRecordatorio.getText().toString());
        values.put("estado", checkBoxCompletada.isChecked() ? 1 : 0);

        db.update("Tareas", values, "id = ?", new String[]{String.valueOf(selectedTareaId)});

        // Actualizar los días de recordatorio
        String selectedOption = (String) spDias.getSelectedItem();
        List<String> diasRecordatorio = new ArrayList<>();

        if (selectedOption.equals("Cada día")) {
            diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        } else if (selectedOption.equals("Cada día (Lun - Vie)")) {
            diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
        } else if (selectedOption.equals("Cada día (Sáb - Dom)")) {
            diasRecordatorio = Arrays.asList("Sábado", "Domingo");
        } else if (!diasPersonalizados.isEmpty()) {
            diasRecordatorio = new ArrayList<>(diasPersonalizados);  // Usamos los días personalizados seleccionados
        }

        // Eliminar días antiguos de la tabla DiasRecordatorio
        db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(selectedTareaId)});

        // Insertar los nuevos días de recordatorio en la tabla DiasRecordatorio
        for (String dia : diasRecordatorio) {
            ContentValues diaValues = new ContentValues();
            diaValues.put("tarea_id", selectedTareaId);
            diaValues.put("dia", dia);
            db.insert("DiasRecordatorio", null, diaValues);
        }

        // Actualizar alarmas de tarea
        cancelarAlarmasViejas();
        programarAlarmas();

        db.close();
        Toast.makeText(this, "Tarea editada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void cancelarAlarmasViejas() {
        // Aquí cancelamos las alarmas viejas, para evitar alarmas duplicadas
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, selectedTareaId, new Intent(this, NotificationReceiver.class),
                PendingIntent.FLAG_MUTABLE); // Agregar FLAG_IMMUTABLE
        alarmManager.cancel(pendingIntent);
    }

    private int obtenerCategoriaIdPorNombre(String nombreCategoria) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int categoriaId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM Categorias WHERE nombre = ?", new String[]{nombreCategoria});

        if (cursor.moveToFirst()) {
            categoriaId = cursor.getInt(0);
            Log.d("IDCATEGORIA", "Numero categoria: " +categoriaId);
        }

        cursor.close();
        return categoriaId;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarAlarmas() {
        // Aquí programamos las nuevas alarmas basándonos en los cambios realizados
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Crear el Intent para NotificationReceiver
        Intent intent = new Intent(this, NotificationReceiver.class);

        // Para que la notificación no salga con el texto "Recordatorio: null" de antes
        intent.putExtra("titulo", etTituloTarea.getText().toString()); // Título de la tarea
        intent.putExtra("descripcion", etDescripcion.getText().toString()); // Descripción de la tarea

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, selectedTareaId, intent,
                PendingIntent.FLAG_IMMUTABLE); // FLAG_IMMUTABLE

        // Recuperar la opción seleccionada en el spinner
        String selectedOption = (String) spDias.getSelectedItem();
        List<String> diasRecordatorio = new ArrayList<>();

        // Determinamos los días de la alarma según la opción seleccionada
        if (selectedOption.equals("Cada día")) {
            diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        } else if (selectedOption.equals("Cada día (Lun - Vie)")) {
            diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
        } else if (selectedOption.equals("Cada día (Sáb - Dom)")) {
            diasRecordatorio = Arrays.asList("Sábado", "Domingo");
        } else if (!diasPersonalizados.isEmpty()) {
            diasRecordatorio = new ArrayList<>(diasPersonalizados); // Usamos los días personalizados
        }

        // Programar las alarmas para los días seleccionados
        programarAlarmasParaDias(alarmManager, pendingIntent, diasRecordatorio);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarAlarmasParaDias(AlarmManager alarmManager, PendingIntent pendingIntent, List<String> diasRecordatorio) {
        Calendar calendar = Calendar.getInstance();

        // Obtener la hora del recordatorio desde el EditText
        int hora = Integer.parseInt(etHoraRecordatorio.getText().toString().split(":")[0]);
        int minuto = Integer.parseInt(etHoraRecordatorio.getText().toString().split(":")[1]);

        for (String dia : diasRecordatorio) {
            int dayOfWeek = obtenerDayOfWeek(dia);
            if (dayOfWeek == -1) continue; // Omite si el día es inválido

            // Configurar el calendario para el próximo día de recordatorio
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, minuto);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Asegúrate de que la fecha sea en el futuro
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1); // Si la fecha es en el pasado, sumamos una semana
            }


            // Crear el Intent para NotificationReceiver
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("titulo", etTituloTarea.getText().toString()); // Título de la tarea
            intent.putExtra("descripcion", etDescripcion.getText().toString()); // Descripción de la tarea


// Crear un PendingIntent único para este día y hora específicos
            int requestCode = selectedTareaId * 10000 + dayOfWeek * 100 + hora * 10 + minuto; // Genera un requestCode único usando el ID de tarea
            PendingIntent pendingIntentDia = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );


// Programar la alarma
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentDia);
            }

        }
    }

    private int obtenerDayOfWeek(String dia) {
        switch (dia) {
            case "Lunes": return Calendar.MONDAY;
            case "Martes": return Calendar.TUESDAY;
            case "Miércoles": return Calendar.WEDNESDAY;
            case "Jueves": return Calendar.THURSDAY;
            case "Viernes": return Calendar.FRIDAY;
            case "Sábado": return Calendar.SATURDAY;
            case "Domingo": return Calendar.SUNDAY;
            default: return -1; // Devuelve -1 si el día no es válido
        }
    }

    private void cancelarEdicion() {
        finish();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            etFecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            etHora.setText(hourOfDay + ":" + minute);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showTimePickerDialogRecordatorio() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            etHoraRecordatorio.setText(hourOfDay + ":" + minute);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
