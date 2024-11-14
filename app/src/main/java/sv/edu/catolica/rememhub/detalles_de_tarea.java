package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.error_al_obtener_la_categor_a_seleccionada;
import static sv.edu.catolica.rememhub.R.string.la_fecha_de_cumplimiento_no_puede_ser_anterior_a_la_fecha_actual;
import static sv.edu.catolica.rememhub.R.string.tarea_editada_con_xito;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import sv.edu.catolica.rememhub.db.DbTareas;

public class detalles_de_tarea extends AppCompatActivity {

    private EditText etTituloTarea, etDescripcion, etFecha, etHora, etHoraRecordatorio;
    private Spinner spTarea, spCategoria, spDias;
    private Button btnCancelar, btnGuardar;
    private List<String> diasPersonalizados = new ArrayList<>();
    private String[] opciones = {"Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};
    private RememhubBD dbHelper;
    private int selectedTareaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_de_tarea);

        inicializarComponentes();
        configurarAdaptadores();
        cargarDatosIniciales();
        configurarListeners();
    }

    private void inicializarComponentes() {
        etTituloTarea = findViewById(R.id.txtTitulo);
        etDescripcion = findViewById(R.id.txtDescripcion);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etHoraRecordatorio = findViewById(R.id.etHoraRecordatorio);
        spTarea = findViewById(R.id.spinnerTarea);
        spCategoria = findViewById(R.id.spinnerCategoria);
        spDias = findViewById(R.id.spDias);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGuardar = findViewById(R.id.btnGuardar);
        dbHelper = new RememhubBD(this);
    }

    private void configurarAdaptadores() {
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDias.setAdapter(adapterDias);
    }

    private void cargarDatosIniciales() {
        cargarTareas();
        cargarCategorias();
    }

    private void mostrarDialogoDiasPersonalizados() {
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        boolean[] diasSeleccionados = new boolean[diasSemana.length];

        // Verifica si ya hay días seleccionados previamente
        for (int i = 0; i < diasSemana.length; i++) {
            diasSeleccionados[i] = diasPersonalizados.contains(diasSemana[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selecciona_los_d_as_de_recordatorio);

        builder.setMultiChoiceItems(diasSemana, diasSeleccionados, (dialog, which, isChecked) -> {
            if (isChecked) {
                diasPersonalizados.add(diasSemana[which]);
            } else {
                diasPersonalizados.remove(diasSemana[which]);
            }
            actualizarTextoSpinner(); // Actualiza el texto del spinner cada vez que se selecciona un día
        });

        builder.setPositiveButton(R.string.aceptar, (dialog, which) -> {
            if (diasPersonalizados.isEmpty()) {
                Toast.makeText(this, R.string.selecciona_al_menos_un_d_a, Toast.LENGTH_SHORT).show();
            } else {
                Log.d("mostrarDialogoDiasPersonalizados", "Días seleccionados: " + diasPersonalizados);
            }
        });

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void configurarListeners() {
        btnGuardar.setOnClickListener(v -> editarTarea());
        btnCancelar.setOnClickListener(v -> finish());
        spDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (opciones[position].equals("Otro...")) {
                    mostrarDialogoDiasPersonalizados();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spTarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarDatosTareaSeleccionada(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTareaId = -1;
            }
        });
    }

    private void cargarTareas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> tareasList = new ArrayList<>();
        List<Integer> tareasIds = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT id, titulo FROM Tareas WHERE papelera = 0 AND estado = 0", null)) {
            while (cursor.moveToNext()) {
                tareasList.add(cursor.getString(1));
                tareasIds.add(cursor.getInt(0));
            }
        }
        ArrayAdapter<String> adapterTareas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tareasList);
        adapterTareas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTarea.setAdapter(adapterTareas);
        spTarea.setTag(tareasIds);
    }

    private void cargarCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> categoriasList = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT nombre FROM Categorias", null)) {
            while (cursor.moveToNext()) categoriasList.add(cursor.getString(0));
        }
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriasList);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);
    }

    private void cargarDatosTareaSeleccionada(int position) {
        List<Integer> tareasIds = (List<Integer>) spTarea.getTag();
        if (tareasIds != null && position < tareasIds.size()) {
            selectedTareaId = tareasIds.get(position);
            cargarDatosTarea();
        } else {
            selectedTareaId = -1;
        }
    }

    private void cargarDatosTarea() {
        if (selectedTareaId == -1) return;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT titulo, descripcion, categoria_id, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado " +
                        "FROM Tareas WHERE id = ?", new String[]{String.valueOf(selectedTareaId)})) {
            if (cursor.moveToFirst()) {
                etTituloTarea.setText(cursor.getString(0));
                etDescripcion.setText(cursor.getString(1));
                etFecha.setText(cursor.getString(3));
                etHora.setText(cursor.getString(4));
                etHoraRecordatorio.setText(cursor.getString(5));
                seleccionarCategoria(cursor.getInt(2));

                // Cargar días de recordatorio
                cargarDiasRecordatorio();
            }
        }
    }

    private void cargarDiasRecordatorio() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT dia FROM DiasRecordatorio WHERE tarea_id = ?", new String[]{String.valueOf(selectedTareaId)})) {
            diasPersonalizados.clear(); // Limpiar la lista antes de cargar
            while (cursor.moveToNext()) {
                diasPersonalizados.add(cursor.getString(0));
            }
        }
        actualizarTextoSpinner(); // Actualiza el texto del spinner
    }

    private void actualizarSpinnerDias() {
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDias.setAdapter(adapterDias);

        // Verifica si hay días personalizados
        if (!diasPersonalizados.isEmpty()) {
            // Si hay días personalizados, selecciona "Otro..."
            spDias.setSelection(adapterDias.getPosition("Otro..."));
        }
    }

    private void seleccionarCategoria(int categoriaId) {
        ArrayAdapter<String> adapterCategorias = (ArrayAdapter<String>) spCategoria.getAdapter();
        if (categoriaId > 0 && categoriaId <= adapterCategorias.getCount()) spCategoria.setSelection(categoriaId - 1);
    }

    private void editarTarea() {
        int categoriaId = obtenerCategoriaIdPorNombre(spCategoria.getSelectedItem().toString());
        if (categoriaId == -1) {
            Toast.makeText(this, "Error al obtener la categoría seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaCumplimientoStr = etFecha.getText().toString();
        String horaCumplimientoStr = etHora.getText().toString();
        String horaRecordatorioStr = etHoraRecordatorio.getText().toString();

        if (!validarFechaHora(fechaCumplimientoStr, horaCumplimientoStr)) return;

        // Cancelar alarmas antiguas
        gestionarAlarmas();

        // Crear ContentValues para la actualización de la tarea
        ContentValues values = crearContentValues(categoriaId, fechaCumplimientoStr, horaCumplimientoStr);
        actualizarTareaEnDB(values);

        // Reprogramar alarmas con los nuevos datos
        configurarAlarmaCumplimiento();
        configurarAlarmaRecordatorio();

        Toast.makeText(this, "Tarea editada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }

    private ContentValues crearContentValues(int categoriaId, String fechaCumplimientoStr, String horaCumplimientoStr) {
        ContentValues values = new ContentValues();
        values.put("titulo", etTituloTarea.getText().toString());
        values.put("descripcion", etDescripcion.getText().toString());
        values.put("categoria_id", categoriaId);
        values.put("fecha_cumplimiento", fechaCumplimientoStr);
        values.put("hora_cumplimiento", horaCumplimientoStr);
        values.put("hora_recordatorio", etHoraRecordatorio.getText().toString());
        // Ya no estamos utilizando el CheckBox, así que no es necesario el estado de completada
        return values;
    }

    private void actualizarTareaEnDB(ContentValues values) {
        if (selectedTareaId == -1) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update("Tareas", values, "id = ?", new String[]{String.valueOf(selectedTareaId)});
    }

    private void gestionarAlarmas() {
        // Cancelar alarmas antiguas
        DbTareas dbTareas = new DbTareas(this);
        dbTareas.eliminarAlarmaTarea(selectedTareaId, this); // Eliminar alarmas antiguas si existen.

        // Guardar nuevas alarmas
        dbTareas.guardarAlarmas(selectedTareaId, diasPersonalizados, etHoraRecordatorio.getText().toString());
    }


    private boolean validarFechaHora(String fechaCumplimientoStr, String horaCumplimientoStr) {
        Calendar fechaHoraCumplimiento = Calendar.getInstance();
        fechaHoraCumplimiento.set(
                Integer.parseInt(fechaCumplimientoStr.split("-")[0]),
                Integer.parseInt(fechaCumplimientoStr.split("-")[1]) - 1,
                Integer.parseInt(fechaCumplimientoStr.split("-")[2]),
                Integer.parseInt(horaCumplimientoStr.split(":")[0]),
                Integer.parseInt(horaCumplimientoStr.split(":")[1])
        );
        if (fechaHoraCumplimiento.before(Calendar.getInstance())) {
            Toast.makeText(this, la_fecha_de_cumplimiento_no_puede_ser_anterior_a_la_fecha_actual, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void actualizarTextoSpinner() {
        if (!diasPersonalizados.isEmpty() && spDias.getSelectedItem().equals("Otro...")) {
            String textoSpinner = String.join(", ", diasPersonalizados);
            View v = spDias.getSelectedView();
            if (v instanceof TextView) {
                ((TextView) v).setText(textoSpinner);
            }
        }
    }

    private void actualizarDiasRecordatorio(SQLiteDatabase db, List<String> diasRecordatorio) {
        // Eliminar días antiguos
        db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(selectedTareaId)});

        // Insertar nuevos días sin el campo hora_recordatorio
        for (String dia : diasRecordatorio) {
            ContentValues diaValues = new ContentValues();
            diaValues.put("tarea_id", selectedTareaId);
            diaValues.put("dia", dia);
            db.insert("DiasRecordatorio", null, diaValues);
        }

        if (diasRecordatorio.isEmpty()) {
            Log.d("actualizarDiasRecordatorio", "La lista de días de recordatorio está vacía.");
        } else {
            Log.d("actualizarDiasRecordatorio", "Días actualizados: " + diasRecordatorio);
        }
    }




    private List<String> obtenerDiasRecordatorio(String selectedOption) {
        List<String> diasRecordatorio;
        if (selectedOption.equals("Cada día")) {
            return Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        } else if (selectedOption.equals("Cada día (Lun - Vie)")) {
            return Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
        } else if (selectedOption.equals("Cada día (Sáb - Dom)")) {
            return Arrays.asList("Sábado", "Domingo");
        } else {
            // Usamos la lista de días personalizados si el usuario seleccionó "Otro..."
            diasRecordatorio = new ArrayList<>(diasPersonalizados);
            Log.d("obtenerDiasRecordatorio", "Días personalizados seleccionados: " + diasRecordatorio);
        }
        return diasRecordatorio;
    }


    @SuppressLint("ScheduleExactAlarm")
    private void configurarAlarmaCumplimiento() {
        Calendar fechaHoraCumplimiento = obtenerFechaHora(etFecha.getText().toString(), etHora.getText().toString());
        if (fechaHoraCumplimiento != null) {
            Intent intentCumplimiento = new Intent(this, NotificationReceiver.class);
            intentCumplimiento.putExtra("tareaId", selectedTareaId);
            intentCumplimiento.putExtra("titulo", etTituloTarea.getText().toString());

            PendingIntent pendingIntentCumplimiento = PendingIntent.getBroadcast(
                    this, selectedTareaId, intentCumplimiento, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, fechaHoraCumplimiento.getTimeInMillis(), pendingIntentCumplimiento);

            Log.d("configurarAlarmaCumplimiento", "Alarma de cumplimiento programada para la tarea ID: "
                    + selectedTareaId + " en " + fechaHoraCumplimiento.getTime().toString());
        }
    }

    // Se asegura de que las alarmas no se repitan innecesariamente, eliminándolas si ya están programadas.
    @SuppressLint("ScheduleExactAlarm")
    private void configurarAlarmaRecordatorio() {
        String[] horaRecordatorioArray = etHoraRecordatorio.getText().toString().split(":");
        int horaRecordatorio = Integer.parseInt(horaRecordatorioArray[0]);
        int minutoRecordatorio = Integer.parseInt(horaRecordatorioArray[1]);

        for (String dia : diasPersonalizados) {
            Calendar recordatorio = Calendar.getInstance();
            recordatorio.set(Calendar.HOUR_OF_DAY, horaRecordatorio);
            recordatorio.set(Calendar.MINUTE, minutoRecordatorio);
            recordatorio.set(Calendar.SECOND, 0);
            recordatorio.set(Calendar.MILLISECOND, 0);

            // Ajustar el día de la semana basado en el valor de `dia`
            switch (dia) {
                case "Lunes": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); break;
                case "Martes": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY); break;
                case "Miércoles": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY); break;
                case "Jueves": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY); break;
                case "Viernes": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY); break;
                case "Sábado": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); break;
                case "Domingo": recordatorio.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); break;
            }

            if (recordatorio.before(Calendar.getInstance())) {
                recordatorio.add(Calendar.WEEK_OF_YEAR, 1); // Si la hora ya pasó, reprogramamos para la próxima semana.
            }

            Intent intentRecordatorio = new Intent(this, ReminderReceiver.class);
            intentRecordatorio.putExtra("tareaId", selectedTareaId);
            intentRecordatorio.putExtra("titulo", etTituloTarea.getText().toString());
            intentRecordatorio.putExtra("descripcion", etDescripcion.getText().toString()); // Incluir la descripción.

            PendingIntent pendingIntentRecordatorio = PendingIntent.getBroadcast(
                    this, selectedTareaId + dia.hashCode(), intentRecordatorio,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntentRecordatorio); // Cancelar alarmas anteriores para evitar duplicados.

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    recordatorio.getTimeInMillis(),
                    pendingIntentRecordatorio
            );

            Log.d("configurarAlarmaRecordatorio", "Alarma de recordatorio programada para la tarea ID: "
                    + selectedTareaId + " el día: " + dia + " a las " + horaRecordatorio + ":" + minutoRecordatorio
                    + " Fecha y hora de activación: " + recordatorio.getTime().toString());
        }
    }


    private Calendar obtenerFechaHora(String fechaStr, String horaStr) {
        Calendar calendar = Calendar.getInstance();
        String[] fecha = fechaStr.split("-");
        String[] hora = horaStr.split(":");

        calendar.set(Calendar.YEAR, Integer.parseInt(fecha[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(fecha[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(hora[1]));
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private int obtenerCategoriaIdPorNombre(String nombreCategoria) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT id FROM Categorias WHERE nombre = ?", new String[]{nombreCategoria})) {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        }
        return -1;
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