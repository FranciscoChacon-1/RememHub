package sv.edu.catolica.rememhub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.*;

import sv.edu.catolica.rememhub.db.DbCategorias;
import sv.edu.catolica.rememhub.db.DbTareas;

public class Añadirtarea extends AppCompatActivity {
    EditText et1, et2, et3, txtTitulo, txtDescripcion;
    Spinner SpCategorias, spDia;
    private String[] opciones = {"Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};
    private List<String> diasPersonalizados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadir_tarea);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        SpCategorias = findViewById(R.id.spinner);
        spDia = findViewById(R.id.spDias);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDia.setAdapter(adapter);

        Button btnCancelar = findViewById(R.id.btncancelar);
        Button btnGuardar = findViewById(R.id.btnguardar);

        List<Categorias> listCat = llenarCategorias();
        ArrayAdapter<Categorias> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCat);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpCategorias.setAdapter(arrayAdapter);

        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (opciones[position].equals("Otro...")) {
                    mostrarDialogoDias();
                } else {
                    diasPersonalizados.clear();
                    actualizarTextoSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGuardar.setOnClickListener(this::Guardar);
        btnCancelar.setOnClickListener(this::Cancelar);
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
                cursor.close();
            }
        }
        dbCategorias.close();
        return listCat;
    }

    public void Cancelar(View view) {
        clearFields();
    }

    private void actualizarTextoSpinner() {
        if (!diasPersonalizados.isEmpty() && spDia.getSelectedItem().equals("Otro...")) {
            String textoSpinner = String.join(", ", diasPersonalizados);
            View v = spDia.getSelectedView();
            if (v instanceof TextView) {
                ((TextView) v).setText(textoSpinner);
            }
        }
    }

    public void Guardar(View view) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivityForResult(intent, 123);
                    return;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }


            DbTareas dbTareas = new DbTareas(Añadirtarea.this);
            Categorias categoriaSeleccionada = (Categorias) SpCategorias.getSelectedItem();
            int categoriaId = categoriaSeleccionada != null ? categoriaSeleccionada.getId() : -1;

            String titulo = txtTitulo.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String fechaCumplimiento = et1.getText().toString();
            String horaCumplimiento = et2.getText().toString();
            String horaRecordatorio = et3.getText().toString();

            if (titulo.isEmpty() || descripcion.isEmpty() || fechaCumplimiento.isEmpty() || horaCumplimiento.isEmpty() || horaRecordatorio.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbTareas.insertarTarea(titulo, descripcion, fechaCreacion, fechaCumplimiento, horaCumplimiento, horaRecordatorio, categoriaId);

            if (id > 0) {
                Toast.makeText(Añadirtarea.this, "Tarea guardada con ID: " + id, Toast.LENGTH_SHORT).show();
                clearFields();

                String selectedOption = (String) spDia.getSelectedItem();
                List<String> diasRecordatorio = new ArrayList<>();

                if (selectedOption.equals("Otro...")) {
                    diasRecordatorio = new ArrayList<>(diasPersonalizados);
                } else {
                    switch (selectedOption) {
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
                    dbTareas.insertarDiaRecordatorio((int) id, dia); // Insertar cada día individualmente
                }

                programarNotificacion(id, titulo, descripcion, diasRecordatorio, horaRecordatorio);
                programarNotificacionCumplimiento(id, titulo, descripcion, fechaCumplimiento, horaCumplimiento);
            } else {
                Toast.makeText(Añadirtarea.this, "Error al guardar tarea", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", "Error guardando tarea: " + e.getMessage());
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarNotificacion(long tareaId, String titulo, String descripcion, List<String> diasRecordatorio, String horaRecordatorio) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String[] horaArray = horaRecordatorio.split(":");

        for (String dia : diasRecordatorio) {
            int dayOfWeek = getDayOfWeek(dia);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaArray[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(horaArray[1]));
            calendar.set(Calendar.SECOND, 0);

            // Ajustar para la próxima semana si está en el pasado
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("tareaId", tareaId);
            intent.putExtra("titulo", titulo);
            intent.putExtra("descripcion", descripcion);

            int requestCode = (int) (tareaId + dayOfWeek);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Usa `setExact` en lugar de `setRepeating` para precisión en cada día específico
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }



    private int getDayOfWeek(String dia) {
        switch (dia) {
            case "Lunes": return Calendar.MONDAY;
            case "Martes": return Calendar.TUESDAY;
            case "Miércoles": return Calendar.WEDNESDAY;
            case "Jueves": return Calendar.THURSDAY;
            case "Viernes": return Calendar.FRIDAY;
            case "Sábado": return Calendar.SATURDAY;
            case "Domingo": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarNotificacionCumplimiento(long tareaId, String titulo, String descripcion, String fechaCumplimiento, String horaCumplimiento) {
        try {
            Calendar calendar = Calendar.getInstance();
            String[] fechaArray = fechaCumplimiento.split("-");
            String[] horaArray = horaCumplimiento.split(":");

            calendar.set(Calendar.YEAR, Integer.parseInt(fechaArray[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(fechaArray[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaArray[2]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaArray[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(horaArray[1]));
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("tareaId", tareaId);
            intent.putExtra("titulo", titulo);
            intent.putExtra("descripcion", descripcion);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) tareaId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Usar `setExact` para asegurar que se envíe justo en la fecha y hora de cumplimiento
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            Log.e("Error", "Error al programar notificación de cumplimiento: " + e.getMessage());
        }
    }



    private void mostrarDialogoDias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona los días");
        builder.setMultiChoiceItems(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"},
                null, (dialog, which, isChecked) -> {
                    String dia = new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}[which];
                    if (isChecked) {
                        diasPersonalizados.add(dia);
                    } else {
                        diasPersonalizados.remove(dia);
                    }
                    actualizarTextoSpinner();
                });

        builder.setPositiveButton("Aceptar", (dialog, which) -> {});
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void clearFields() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        et1.setText("");
        et2.setText("");
        et3.setText("");
    }


    //Codigo necesario para la vista
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                //(view, year1, month1, dayOfMonth) -> tvFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                (view, year1, month1, dayOfMonth) -> et1.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth),
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
            et3.setText(String.format("%02d:%02d", hour, minute));
        }, 10, 30, true);
        d.show();
    }


    public void mostrarHorario(View view) {
        int hora = 0, minuto = 0;

        if (!et2.getText().toString().isEmpty()) {
            String[] horaArray = et2.getText().toString().split(":");
            hora = Integer.parseInt(horaArray[0]);
            minuto = Integer.parseInt(horaArray[1]);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(Añadirtarea.this,
                (view1, hourOfDay, minute) -> et2.setText(String.format("%02d:%02d", hourOfDay, minute)), hora, minuto, true);

        timePickerDialog.show();
    }

}
