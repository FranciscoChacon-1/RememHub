package sv.edu.catolica.rememhub;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class activity_configuracion extends AppCompatActivity {

    private TextView tvSelectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);

        // Inicializar componentes del layout
        tvSelectedTime = findViewById(R.id.tv_selected_time);
        TextView tvNotificacionExtra = findViewById(R.id.tv_notificacion_extra);
        CheckBox checkBoxVibracion = findViewById(R.id.checkbox_vibracion);

        // Listener para mostrar el PopupMenu cuando se haga clic en el TextView
        tvNotificacionExtra.setOnClickListener(this::showPopupMenu);

        // Listener para el CheckBox de Vibración (si se necesita realizar alguna acción al cambiar su estado)
        checkBoxVibracion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Aquí puedes agregar la lógica para activar/desactivar vibración
            if (isChecked) {
                // Activar vibración
            } else {
                // Desactivar vibración
            }
        });
    }

    // Método para mostrar el PopupMenu
    private void showPopupMenu(View view) {
        // Crear una instancia de PopupMenu
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popop_menu_notification, popup.getMenu()); // Inflar el menú desde recursos

        // Agregar lógica cuando se seleccionen las opciones del menú
        popup.setOnMenuItemClickListener(item -> {
            // Obtener el título del ítem seleccionado (como un String)
            String selectedItemTitle = Objects.requireNonNull(item.getTitle()).toString();

            // Comparar el título del ítem usando switch
            switch (selectedItemTitle) {
                case "5 minutos antes":
                    tvSelectedTime.setText("5 minutos antes");
                    return true;
                case "15 minutos antes":
                    tvSelectedTime.setText("15 minutos antes");
                    return true;
                case "30 minutos antes":
                    tvSelectedTime.setText("30 minutos antes");
                    return true;
                case "1 hora antes":
                    tvSelectedTime.setText("1 hora antes");
                    return true;
                case "2 horas antes":
                    tvSelectedTime.setText("2 horas antes");
                    return true;
                default:
                    return false;
            }
        });


        // Mostrar el PopupMenu
        popup.show();
    }
}
