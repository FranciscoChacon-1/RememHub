package sv.edu.catolica.rememhub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import android.util.Log;


import sv.edu.catolica.rememhub.db.DbCategorias;
import sv.edu.catolica.rememhub.db.DbTareas;



public class Añadirtarea extends AppCompatActivity {
    EditText et1, et2, et3, txtTitulo, txtDescripcion;
    Spinner SpCategorias, spDia;
    private String[] opciones = {"No repetir", "Cada día", "Cada día (Lun - Vie)", "Cada día (Sáb - Dom)", "Otro..."};

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

        List<Categorias> listCat = llenarCategorias(); // Lista de objetos Categorias
        ArrayAdapter<Categorias> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCat);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpCategorias.setAdapter(arrayAdapter);

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
                cursor.close(); // Asegúrate de cerrar el cursor
            }
        }
        dbCategorias.close();
        return listCat;
    }

    public void Cancelar(View view) {
        clearFields();
    }

    public void mostrarHorario(View view) {
        int hora = 0, minuto = 0;

        if (!et2.getText().toString().isEmpty()) {
            String[] horaArray = et2.getText().toString().split(":");
            hora = Integer.parseInt(horaArray[0]);
            minuto = Integer.parseInt(horaArray[1]);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(Añadirtarea.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et2.setText(String.format("%02d:%02d", hourOfDay, minute));  // Mostrar la hora seleccionada en el EditText
                    }
                }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void mostrarDialogoDias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona los días personalizados");
        builder.setItems(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"},
                (dialog, which) -> {
                    // Acciones al seleccionar un día
                });
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.show();
    }


    public void Guardar(View view) {
        try {
            // Verificar y solicitar permisos necesarios
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivityForResult(intent, 123);
                    return;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }

            // Guardar tarea
            DbTareas dbTareas = new DbTareas(Añadirtarea.this);
            Categorias categoriaSeleccionada = (Categorias) SpCategorias.getSelectedItem();
            int categoriaId = categoriaSeleccionada != null ? categoriaSeleccionada.getId() : -1;

            String titulo = txtTitulo.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String fechaCumplimiento = et1.getText().toString();
            String horaCumplimiento = et2.getText().toString();
            String horaRecordatorio = et3.getText().toString();

            if (titulo.isEmpty() || descripcion.isEmpty() || fechaCumplimiento.isEmpty() || horaCumplimiento.isEmpty() || horaRecordatorio.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato de hora
            if (!horaCumplimiento.matches("^([01]?[0-9]|2[0-3]):([0-5]?[0-9])$") || !horaRecordatorio.matches("^([01]?[0-9]|2[0-3]):([0-5]?[0-9])$")) {
                Toast.makeText(this, "Por favor, ingresa una hora válida en formato HH:mm", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbTareas.insertarTarea(titulo, descripcion, categoriaId, fechaCumplimiento, horaCumplimiento, horaRecordatorio);
            if (id > 0) {
                Toast.makeText(Añadirtarea.this, "Tarea guardada con ID: " + id, Toast.LENGTH_SHORT).show();
                clearFields();

                // Preparar parámetros para la notificación
                String[] horaArray = horaRecordatorio.split(":");
                int hora = Integer.parseInt(horaArray[0]);
                int minuto = Integer.parseInt(horaArray[1]);

                // Determinar si la tarea se repite, y en qué días
                String selectedOption = (String) spDia.getSelectedItem();
                List<String> diasRecordatorio = new ArrayList<>();

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
                    case "No repetir":
                        diasRecordatorio = new ArrayList<>(); // Lista vacía
                        break;
                    default:
                        Toast.makeText(this, "Opción de repetición no reconocida", Toast.LENGTH_SHORT).show();
                        return;
                }

                // Insertar los días en la base de datos
                for (String dia : diasRecordatorio) {
                    dbTareas.insertarDiaRecordatorio((int) id, dia); // Aquí asumo que tienes un método para insertar los días en la base de datos
                }

                // Programar la notificación con la lista de días
                programarNotificacion(titulo, descripcion, diasRecordatorio, hora, minuto);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar la tarea: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @SuppressLint("ScheduleExactAlarm")
    private void programarNotificacion(String titulo, String descripcion, List<String> diasRecordatorio, int hora, int minuto) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for (String dia : diasRecordatorio) {
            int dayOfWeek = obtenerDayOfWeek(dia);
            if (dayOfWeek == -1) continue; // Omite si el día es inválido

            // Configura el calendario para el próximo día de recordatorio
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, minuto);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Asegúrate de que la fecha sea en el futuro
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            // Crear un PendingIntent único para este día y hora específicos
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("titulo", titulo);
            intent.putExtra("descripcion", descripcion);

            int requestCode = dayOfWeek * 100 + hora * 10 + minuto; // Genera un requestCode único
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);  // Usamos FLAG_IMMUTABLE


            // Programar la alarma
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }



    private void clearFields() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        et1.setText("");
        et2.setText("");
        et3.setText("");
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

    public void mostrarCalendario(View v){
        showDatePickerDialog();
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
}



