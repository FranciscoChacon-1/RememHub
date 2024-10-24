package sv.edu.catolica.rememhub;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_historial extends AppCompatActivity {

    private Button dateButton;
    private Calendar selectedDate;
    private LinearLayout taskContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        dateButton = findViewById(R.id.dateButton);
        //taskContainer = findViewById(R.id.main);

        // Selector de fecha
        dateButton.setOnClickListener(v -> openDatePicker());

        // Inicializamos la lista de tareas (una por ahora)
        initTask();
    }

    private void initTask() {
        // Ejemplo de tarea
        CheckBox taskCheckBox = findViewById(R.id.taskCheckBox);
        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskCategory = findViewById(R.id.taskCategory);

        taskTitle.setText("Tarea 1");
        taskCategory.setText("Categoría 1");

        // Aquí puedes implementar la lógica de la base de datos para obtener las tareas
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        // Crear el DatePickerDialog con un botón de "Cancel"
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar;  // Guardar la fecha seleccionada
                    updateDateButton(calendar);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Añadir un botón de "Cancel" al DatePickerDialog
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            if (which == DatePickerDialog.BUTTON_NEGATIVE) {
                // Restablecer la fecha seleccionada y el texto del botón
                selectedDate = null;  // Borrar la fecha seleccionada
                dateButton.setText(R.string.fecha);  // Texto por defecto en el botón
            }
        });

        datePicker.show();
    }

    private void updateDateButton(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateButton.setText(dateFormat.format(calendar.getTime()));

        // Filtrar las tareas según la fecha seleccionada
        filterTasksByDate(calendar);
    }

    private void filterTasksByDate(Calendar selectedDate) {
        // Lógica para filtrar las tareas por la fecha seleccionada
        // Aquí debes hacer la consulta a tu base de datos para obtener las tareas
        // correspondientes a esa fecha.
        // Si no hay una fecha seleccionada, muestra todas las tareas.
    }
}
