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
        String query = "SELECT id, titulo FROM Tareas WHERE papelera = 0";


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
        String categoriaSeleccionada = spCategoria.getSelectedItem().toString();
        int categoriaId = obtenerCategoriaIdPorNombre(categoriaSeleccionada);

        if (categoriaId == -1) {
            Toast.makeText(this, "Error al obtener la categoría seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar datos de la tarea en la base de datos
        ContentValues values = new ContentValues();
        values.put("titulo", etTituloTarea.getText().toString());
        values.put("descripcion", etDescripcion.getText().toString());
        values.put("categoria_id", categoriaId);
        values.put("fecha_cumplimiento", etFecha.getText().toString());
        values.put("hora_cumplimiento", etHora.getText().toString());
        values.put("hora_recordatorio", etHoraRecordatorio.getText().toString());
        values.put("estado", checkBoxCompletada.isChecked() ? 1 : 0);

        db.update("Tareas", values, "id = ?", new String[]{String.valueOf(selectedTareaId)});

        // Obtener días de recordatorio seleccionados
        String selectedOption = (String) spDias.getSelectedItem();
        List<String> diasRecordatorio = obtenerDiasRecordatorio(selectedOption);

        // Actualizar días de recordatorio en la base de datos
        actualizarDiasRecordatorio(db, diasRecordatorio);

        // Cancelar y reprogramar alarmas con nueva información
        cancelarAlarmasViejas();
        if (!checkBoxCompletada.isChecked()) {  // Solo si la tarea no está completada
            programarAlarmas();  // Esto incluye alarmas para recordatorios y cumplimiento
        }

        db.close();
        Toast.makeText(this, "Tarea editada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }

    private List<String> obtenerDiasRecordatorio(String selectedOption) {
        if (selectedOption.equals("Cada día")) {
            return Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        } else if (selectedOption.equals("Cada día (Lun - Vie)")) {
            return Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
        } else if (selectedOption.equals("Cada día (Sáb - Dom)")) {
            return Arrays.asList("Sábado", "Domingo");
        } else {
            return new ArrayList<>(diasPersonalizados);
        }
    }

    private void actualizarDiasRecordatorio(SQLiteDatabase db, List<String> diasRecordatorio) {
        // Eliminar días antiguos
        db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(selectedTareaId)});

        // Insertar nuevos días
        for (String dia : diasRecordatorio) {
            ContentValues diaValues = new ContentValues();
            diaValues.put("tarea_id", selectedTareaId);
            diaValues.put("dia", dia);
            db.insert("DiasRecordatorio", null, diaValues);
        }
    }

    private void cancelarAlarmasViejas() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager == null) return;

        // Crear un Intent para el receptor de notificaciones
        Intent intent = new Intent(this, NotificationReceiver.class);

        // Obtener los días de recordatorio seleccionados
        String selectedOption = (String) spDias.getSelectedItem();
        List<String> diasRecordatorio = obtenerDiasRecordatorio(selectedOption);

        // Iterar sobre cada día y hora para cancelar todas las alarmas individuales
        for (String dia : diasRecordatorio) {
            int dayOfWeek = obtenerDayOfWeek(dia);

            // Asegúrate de cancelar cada combinación de horas/minutos posibles para los recordatorios
            try {
                int hora = Integer.parseInt(etHoraRecordatorio.getText().toString().split(":")[0]);
                int minuto = Integer.parseInt(etHoraRecordatorio.getText().toString().split(":")[1]);

                int requestCode = selectedTareaId * 10000 + dayOfWeek * 100 + hora * 10 + minuto; // Igual que en programarAlarmasParaDias

                PendingIntent pendingIntentDia = PendingIntent.getBroadcast(
                        this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );

                // Cancelar la alarma
                alarmManager.cancel(pendingIntentDia);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Configurar la notificación de cumplimiento
        programarAlarmaCumplimiento(alarmManager);

        // Configurar las notificaciones de recordatorio
        programarAlarmasParaRecordatorios(alarmManager);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarAlarmaCumplimiento(AlarmManager alarmManager) {
        // Obtener fecha y hora de cumplimiento
        String[] fechaParts = etFecha.getText().toString().split("-");
        String[] horaParts = etHora.getText().toString().split(":");
        int año = Integer.parseInt(fechaParts[0]);
        int mes = Integer.parseInt(fechaParts[1]) - 1; // Meses comienzan desde 0 en Calendar
        int día = Integer.parseInt(fechaParts[2]);
        int hora = Integer.parseInt(horaParts[0]);
        int minuto = Integer.parseInt(horaParts[1]);

        // Crear el calendario con fecha y hora de cumplimiento
        Calendar calendar = Calendar.getInstance();
        calendar.set(año, mes, día, hora, minuto, 0);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("titulo", etTituloTarea.getText().toString());
        intent.putExtra("descripcion", etDescripcion.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, selectedTareaId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null && calendar.getTimeInMillis() > System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void programarAlarmasParaRecordatorios(AlarmManager alarmManager) {
        // Crear intent para el receptor de notificaciones
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("titulo", etTituloTarea.getText().toString());
        intent.putExtra("descripcion", etDescripcion.getText().toString());

        // Obtener los días de recordatorio según la opción seleccionada
        String selectedOption = (String) spDias.getSelectedItem();
        List<String> diasRecordatorio = obtenerDiasRecordatorio(selectedOption);

        // Programar alarmas para cada día
        for (String dia : diasRecordatorio) {
            programarAlarmaParaDia(alarmManager, dia, intent);
        }
    }



    @SuppressLint("ScheduleExactAlarm")
    private void programarAlarmaParaDia(AlarmManager alarmManager, String dia, Intent intent) {
        // Crea un calendario para la fecha del recordatorio según el día especificado
        Calendar calendar = Calendar.getInstance();

        switch (dia) {
            case "Lunes":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case "Martes":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case "Miércoles":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case "Jueves":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case "Viernes":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
            case "Sábado":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                break;
            case "Domingo":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
        }

        // Configura la hora del recordatorio
        calendar.set(Calendar.HOUR_OF_DAY, 9);  // Cambia por la hora de recordatorio deseada
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Genera el PendingIntent único para el día
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, dia.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Cancelar cualquier alarma existente con el mismo PendingIntent
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        // Programa la nueva alarma si el tiempo es válido
        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                //(view, year1, month1, dayOfMonth) -> tvFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                (view, year1, month1, dayOfMonth) -> etFecha.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth),
                //'2024-10-03'
                year, month, day
        );
        datePickerDialog.show();
    }

    public void mostrarCalendario(View v){
        showDatePickerDialog();
    }

    public void mostrarRecordatorio(View V) {
        TimePickerDialog d = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            // Formato de hora con dos dígitos para hora y minuto
            etHoraRecordatorio.setText(String.format("%02d:%02d", hour, minute));
        }, 10, 30, true);
        d.show();
    }


    public void mostrarHorario(View view) {
        int hora = 0, minuto = 0;

        if (!etHora.getText().toString().isEmpty()) {
            String[] horaArray = etHora.getText().toString().split(":");
            hora = Integer.parseInt(horaArray[0]);
            minuto = Integer.parseInt(horaArray[1]);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(detalles_de_tarea.this,
                (view1, hourOfDay, minute) -> etHora.setText(String.format("%02d:%02d", hourOfDay, minute)), hora, minuto, true);

        timePickerDialog.show();
    }
}