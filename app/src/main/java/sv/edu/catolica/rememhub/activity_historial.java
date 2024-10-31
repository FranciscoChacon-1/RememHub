package sv.edu.catolica.rememhub;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_historial extends AppCompatActivity {

    private Button dateButton;
    private Calendar selectedDate;
    private LinearLayout taskContainer;
    private RememhubBD dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        dbHelper = new RememhubBD(this);
        dateButton = findViewById(R.id.dateButton);
        taskContainer = findViewById(R.id.taskContainer); // Contenedor para las tareas dinámicas

        dateButton.setOnClickListener(v -> openDatePicker());

        // Cargar y mostrar tareas de la base de datos
        loadTasks();
    }

    private void loadTasks() {
        taskContainer.removeAllViews(); // Limpiar contenedor antes de agregar nuevas tareas

        // Abrir base de datos en modo lectura
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT T.id, T.titulo, T.fecha_cumplimiento, C.nombre AS categoria FROM Tareas T " +
                "LEFT JOIN Categorias C ON T.categoria_id = C.id", null);

        // Iterar sobre los resultados y crear vistas para cada tarea
        if (cursor.moveToFirst()) {
            do {
                View taskView = LayoutInflater.from(this).inflate(R.layout.task_item_layout, taskContainer, false);

                CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
                TextView taskTitle = taskView.findViewById(R.id.taskTitle);
                TextView taskDate = taskView.findViewById(R.id.taskDate);
                TextView taskCategory = taskView.findViewById(R.id.taskCategory);

                String title = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));

                taskTitle.setText(title);
                taskDate.setText(date != null ? date : "Sin fecha");
                taskCategory.setText(category != null ? category : "Sin categoría");

                // Agregar la vista de tarea al contenedor
                taskContainer.addView(taskView);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
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

        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            if (which == DatePickerDialog.BUTTON_NEGATIVE) {
                selectedDate = null;
                dateButton.setText(R.string.fecha);
            }
        });

        datePicker.show();
    }

    private void updateDateButton(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateButton.setText(dateFormat.format(calendar.getTime()));

        filterTasksByDate(calendar);
    }

    private void filterTasksByDate(Calendar selectedDate) {
        // Aquí podrías implementar el filtrado de tareas según la fecha seleccionada
        loadTasks(); // Llamamos a loadTasks para cargar todas las tareas
    }
}
