package sv.edu.catolica.rememhub;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import android.util.Log;

public class activity_resumen extends AppCompatActivity {
    private PieChart pieChartAllTasks;
    private PieChart pieChartByCategory;
    private Spinner catSpinner;
    private static final String TAG = "activity_resumenn"; // Definir el TAG para los logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        catSpinner = findViewById(R.id.categorySpinner);
        pieChartAllTasks = findViewById(R.id.pieChartAllTasks);
        pieChartByCategory = findViewById(R.id.pieChartByCategory);

        cargarCategorias(); // Mostrar las categorias en el spinner
        cargarTodasLasTareas();
        setupPieChart(pieChartByCategory,0,0);


        // Evento para actualizar el gráfico por categoría al cambiar la selección en el spinner
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long id) {
                String catSeleccionada = catSpinner.getSelectedItem().toString();
                actualizarCategorias(catSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //No hacer nada
            }
        });
    }

    private void cargarCategorias()
    {
        RememhubBD dbHelper = new RememhubBD(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> categorias =  new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT nombre FROM Categorias", null);
        while (cursor.moveToNext())
        {
            // Para agregar el 'nombre' de la tabla Categorias
            categorias.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(adapter);
    }

    private void actualizarCategorias(String categoria) {
        RememhubBD dbHelper = new RememhubBD(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int completada = 0;
        int incompleta = 0;

        // Log para verificar la categoría seleccionada
        Log.d(TAG, "Categoría seleccionada: " + categoria);

        Cursor cursor = db.rawQuery("SELECT id FROM Categorias WHERE nombre = ?", new String[]{categoria});
        if (cursor.moveToFirst()) {
            int categoryId = cursor.getInt(0);
            Log.d(TAG, "ID de la categoría: " + categoryId); // Log para verificar el ID de la categoría

            // Contar tareas completadas e incompletas para la categoría seleccionada
            Cursor taskCursor = db.rawQuery(
                    "SELECT estado, COUNT(*) FROM Tareas WHERE categoria_id = ? GROUP BY estado",
                    new String[]{String.valueOf(categoryId)}
            );

            if (taskCursor != null && taskCursor.getCount() > 0) {
                while (taskCursor.moveToNext()) {
                    int estado = taskCursor.getInt(0);
                    int cuenta = taskCursor.getInt(1);

                    // Log para verificar los valores de estado y cuenta de tareas
                    Log.d(TAG, "Estado: " + estado + ", Cuenta: " + cuenta);

                    if (estado == 1) {
                        completada += cuenta; // Sumar tareas completadas
                    } else if (estado == 0) {
                        incompleta += cuenta; // Sumar tareas incompletas
                    }
                }
            }
            assert taskCursor != null;
            taskCursor.close();
        } else {
            Log.d(TAG, getString(R.string.no_se_encontr_la_categor_a) + categoria); // Log si no se encuentra la categoría
        }
        cursor.close();
        db.close();

        // Log para verificar el conteo final de tareas completadas e incompletas
        Log.d(TAG, getString(R.string.tareas_completadas) + completada + getString(R.string.tareas_incompletas) + incompleta);

        // Actualizar gráfico con los datos obtenidos
        setupPieChart(pieChartByCategory, completada, incompleta);
    }



    private void setupPieChart(PieChart pieChart, int completed, int incomplete) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, getString(R.string.completadas)));
        entries.add(new PieEntry(incomplete, getString(R.string.sin_completar)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{ R.color.colorPrimary, R.color.colorAccent }, this);

        PieData data = new PieData(dataSet);

        // Cambiar el color del texto según el modo de tema
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            data.setValueTextColor(Color.WHITE); // Texto en blanco para modo noche
        } else {
            data.setValueTextColor(Color.BLACK); // Texto en negro para modo día
        }

        data.setValueTextSize(14f); // Tamaño del texto
        pieChart.setData(data);

        // Deshabilitar la descripción
        pieChart.getDescription().setEnabled(false);

        // Cambiar el color del centro del gráfico a gris
        pieChart.setHoleColor(Color.GRAY);

        pieChart.invalidate(); // Refrescar el gráfico
    }


    private void cargarTodasLasTareas() {
        RememhubBD dbHelper = new RememhubBD(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int completada = 0;
        int incompleta = 0;

        Cursor cursor = db.rawQuery("SELECT estado, COUNT(*) FROM Tareas GROUP BY estado", null);
        while (cursor.moveToNext()) {
            int estado = cursor.getInt(0);
            int cuenta = cursor.getInt(1);
            if (estado == 1) {
                completada = cuenta;
            } else {
                incompleta = cuenta;
            }
        }
        cursor.close();
        db.close();

        setupPieChart(pieChartAllTasks, completada, incompleta);
    }


}