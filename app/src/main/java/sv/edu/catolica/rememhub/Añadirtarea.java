package sv.edu.catolica.rememhub;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class Añadirtarea  extends AppCompatActivity{
    EditText et1, et2,et3;
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

    }
    public void mostrarCalendario(View v){
        DatePickerDialog d=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                et1.setText(i2+"/"+(i1+1)+"/"+i);
            }
        },2024,0,1);
        d.show();
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
}
