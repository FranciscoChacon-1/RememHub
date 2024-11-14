package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.error_al_guardar_tarea;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.*;

import sv.edu.catolica.rememhub.db.DbTareas;

public class Anadirtarea extends AppCompatActivity {
    private EditText etFechaCumplimiento, etHoraCumplimiento, etHoraRecordatorio, txtTitulo, txtDescripcion;
    private Spinner spCategorias, spDias;
    private final List<String> diasPersonalizados = new ArrayList<>();
    private final String[] opcionesDias = {"Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};
    private RememhubBD dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadir_tarea);

        dbHelper = new RememhubBD(this);
        inicializarVistas();
        configurarAdaptadores();
        configurarEventos();
    }

    private void inicializarVistas() {
        etFechaCumplimiento = findViewById(R.id.etFechaCumplimiento);
        etHoraCumplimiento = findViewById(R.id.etHoraCumplimiento);
        etHoraRecordatorio = findViewById(R.id.etHoraRecordatorio);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spCategorias = findViewById(R.id.spCategorias);
        spDias = findViewById(R.id.spDias);
    }

    private void configurarAdaptadores() {
        // Configurar el Spinner para días
        ArrayAdapter<String> diasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesDias);
        diasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDias.setAdapter(diasAdapter);

        // Configurar el Spinner para categorías usando ArrayAdapter con objetos Categorias
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> categoriasList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT nombre FROM Categorias", null)) {
            while (cursor.moveToNext()) categoriasList.add(cursor.getString(0));
        }
        ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriasList);
        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(categoriasAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private void configurarEventos() {
        spDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (opcionesDias[position].equals("Otro...")) {
                    mostrarDialogoDias();
                } else {
                    diasPersonalizados.clear();
                    actualizarTextoSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.btnGuardar).setOnClickListener(this::guardarTarea);
        findViewById(R.id.btnCancelar).setOnClickListener(view -> limpiarCampos());
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void guardarTarea(View view) {
        if (!verificarPermisos()) return;

        String titulo = txtTitulo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String fechaCumplimiento = etFechaCumplimiento.getText().toString();
        String horaCumplimiento = etHoraCumplimiento.getText().toString();
        String horaRecordatorio = etHoraRecordatorio.getText().toString();

        if (camposIncompletos(titulo, descripcion, fechaCumplimiento, horaCumplimiento, horaRecordatorio)) return;
        if (fechaCumplimientoAnterior()) {
            return;
        }


        int categoriaId  = obtenerCategoriaIdPorNombre(spCategorias.getSelectedItem().toString());
        if (categoriaId == -1) {
            Toast.makeText(this, R.string.error_al_obtener_la_categor_a_seleccionada, Toast.LENGTH_SHORT).show();
            return;
        }
        long tareaId = new DbTareas(this).insertarTarea(titulo, descripcion, obtenerFechaActual(), fechaCumplimiento, horaCumplimiento, horaRecordatorio, categoriaId);

        if (tareaId > 0) {
            procesarDiasRecordatorio(tareaId, (String) spDias.getSelectedItem(), horaRecordatorio);
            programarNotificacionCumplimiento(tareaId, titulo, descripcion, fechaCumplimiento, horaCumplimiento);
            limpiarCampos();
        } else {
            Toast.makeText(this, error_al_guardar_tarea, Toast.LENGTH_SHORT).show();
        }
    }

    private int obtenerCategoriaIdPorNombre(String nombreCategoria) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT id FROM Categorias WHERE nombre = ?", new String[]{nombreCategoria})) {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        }
        return -1;
    }

    private boolean verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM), 123);
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return false;
        }
        return true;
    }

    private boolean camposIncompletos(String... campos) {
        for (String campo : campos) {
            if (campo.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private boolean fechaCumplimientoAnterior() {
        String fechaCumplimientoStr = etFechaCumplimiento.getText().toString();
        String horaCumplimientoStr = etHoraCumplimiento.getText().toString();
        Calendar fechaHoraCumplimiento = Calendar.getInstance();

        try {
            fechaHoraCumplimiento.set(
                    Integer.parseInt(fechaCumplimientoStr.split("-")[0]),   // Año
                    Integer.parseInt(fechaCumplimientoStr.split("-")[1]) - 1, // Mes
                    Integer.parseInt(fechaCumplimientoStr.split("-")[2]),   // Día
                    Integer.parseInt(horaCumplimientoStr.split(":")[0]),    // Hora
                    Integer.parseInt(horaCumplimientoStr.split(":")[1])     // Minuto
            );

            if (fechaHoraCumplimiento.before(Calendar.getInstance())) {
                Toast.makeText(this, "La fecha de cumplimiento no puede ser anterior a la fecha actual", Toast.LENGTH_SHORT).show();
                return true; // Detener el proceso si la fecha de cumplimiento es anterior a la actual
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error en el formato de fecha o hora", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private void procesarDiasRecordatorio(long tareaId, String seleccion, String horaRecordatorio) {

        DbTareas dbTareas = new DbTareas(Anadirtarea.this);
        List<String> diasRecordatorio = new ArrayList<>();

        if (seleccion.equals("Otro...")) {
            diasRecordatorio = new ArrayList<>(diasPersonalizados);
        } else {
            switch (seleccion) {
                case "Cada día":
                    diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
                    break;
                case "Cada día (Lun - Vie)":
                    diasRecordatorio = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
                    break;
                case "Cada día (Sáb - Dom)":
                    diasRecordatorio = Arrays.asList("Sábado", "Domingo");
                    break;
            }
        }

        // Insertar los días en la base de datos
        for (String dia : diasRecordatorio) {
            dbTareas.insertarDiaRecordatorio((int) tareaId, dia); // Insertar cada día individualmente
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            programarNotificacionRecordatorio(tareaId, txtTitulo.getText().toString(), txtDescripcion.getText().toString(), diasRecordatorio, horaRecordatorio);
        }

    }
   // Recordatorios
   @SuppressLint("ScheduleExactAlarm")
   @RequiresApi(api = Build.VERSION_CODES.S) // Asegúrate de que tu método maneje la API 31+
   private void programarNotificacionRecordatorio(long tareaId, String titulo, String descripcion, List<String> diasRecordatorio, String horaRecordatorio) {
       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       String[] horaArray = horaRecordatorio.split(":");
       for (String dia : diasRecordatorio) {
           Calendar calendar = obtenerFechaRecordatorio(dia, horaArray);
           if (calendar.before(Calendar.getInstance())) calendar.add(Calendar.WEEK_OF_YEAR, 1);

           // Crear el PendingIntent
           PendingIntent pendingIntent = crearPendingIntent(tareaId, titulo, descripcion, dia);

           alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
       }
   }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(api = Build.VERSION_CODES.S) // Asegúrate de que tu método maneje la API 31+
    private void programarNotificacionCumplimiento(long tareaId, String titulo, String descripcion, String fechaCumplimiento, String horaCumplimiento) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar fechaHoraCumplimiento = Calendar.getInstance();
        try {
            String[] fechaArray = fechaCumplimiento.split("-");
            String[] horaArray = horaCumplimiento.split(":");
            fechaHoraCumplimiento.set(
                    Integer.parseInt(fechaArray[0]),
                    Integer.parseInt(fechaArray[1]) - 1,
                    Integer.parseInt(fechaArray[2]),
                    Integer.parseInt(horaArray[0]),
                    Integer.parseInt(horaArray[1]),
                    0);

            if (fechaHoraCumplimiento.after(Calendar.getInstance())) {
                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("tareaId", tareaId);
                intent.putExtra("titulo", titulo);
                intent.putExtra("descripcion", descripcion);

                // Crear el PendingIntent con
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) tareaId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Programar la notificación de cumplimiento
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, fechaHoraCumplimiento.getTimeInMillis(), pendingIntent);
                Log.e("Fechacump", "Hora cumplimiento: " + horaCumplimiento);
                Log.e("Fechacump", "FechaHoracumplimiento: " + fechaHoraCumplimiento.getTimeInMillis());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al programar notificación de cumplimiento", Toast.LENGTH_SHORT).show();
            Log.e("Error", "Error en formato de fecha o hora para cumplimiento: " + e.getMessage());
        }
    }


    private Calendar obtenerFechaRecordatorio(String dia, String[] horaArray) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(dia));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(horaArray[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private PendingIntent crearPendingIntent(long tareaId, String titulo, String descripcion, String dia) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("tareaId", tareaId);
        intent.putExtra("titulo", titulo);
        intent.putExtra("descripcion", descripcion);

        // Crear el PendingIntent
        return PendingIntent.getBroadcast(this, (int) (tareaId + getDayOfWeek(dia)), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private int getDayOfWeek(String dia) {
        switch (dia) {
            case "Martes": return Calendar.TUESDAY;
            case "Miércoles": return Calendar.WEDNESDAY;
            case "Jueves": return Calendar.THURSDAY;
            case "Viernes": return Calendar.FRIDAY;
            case "Sábado": return Calendar.SATURDAY;
            case "Domingo": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        etFechaCumplimiento.setText("");
        etHoraCumplimiento.setText("");
        etHoraRecordatorio.setText("");
        spCategorias.setSelection(0);
        spDias.setSelection(0);
    }

    private void mostrarDialogoDias() {
        boolean[] diasSeleccionados = new boolean[7];
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar días")
                .setMultiChoiceItems(dias, diasSeleccionados, (dialog, which, isChecked) -> {
                    if (isChecked) diasPersonalizados.add(dias[which]);
                    else diasPersonalizados.remove(dias[which]);
                })
                .setPositiveButton("Aceptar", (dialog, which) -> actualizarTextoSpinner())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                //(view, year1, month1, dayOfMonth) -> tvFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                (view, year1, month1, dayOfMonth) -> etFechaCumplimiento.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth),
                //'2024-10-03'
                year, month, day
        );
        datePickerDialog.show();
    }

    public void mostrarCalendario(View v){
        showDatePickerDialog();
    }

    public void mostrarRecordatorio(View V) {
        @SuppressLint("DefaultLocale") TimePickerDialog d = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            // Formato de hora con dos dígitos para hora y minuto
            etHoraRecordatorio.setText(String.format("%02d:%02d", hour, minute));
        }, 10, 30, true);
        d.show();
    }


    public void mostrarHorario(View view) {
        int hora = 0, minuto = 0;

        if (!etHoraCumplimiento.getText().toString().isEmpty()) {
            String[] horaArray = etHoraCumplimiento.getText().toString().split(":");
            hora = Integer.parseInt(horaArray[0]);
            minuto = Integer.parseInt(horaArray[1]);
        }

        @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog( Anadirtarea.this,
                (view1, hourOfDay, minute) -> etHoraCumplimiento.setText(String.format("%02d:%02d", hourOfDay, minute)), hora, minuto, true);

        timePickerDialog.show();
    }
}