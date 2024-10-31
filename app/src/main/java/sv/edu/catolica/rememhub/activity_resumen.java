package sv.edu.catolica.rememhub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;

public class activity_resumen extends AppCompatActivity {
    private PieChart pieChartAllTasks;
    private PieChart pieChartByCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        pieChartAllTasks = findViewById(R.id.pieChartAllTasks);
        pieChartByCategory = findViewById(R.id.pieChartByCategory);

        setupPieChart(pieChartAllTasks);
        setupPieChart(pieChartByCategory);
    }

    private void setupPieChart(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8, "Completadas"));
        entries.add(new PieEntry(4, "Sin completar"));

        PieDataSet dataSet = new PieDataSet(entries, "Tareas");
        dataSet.setColors(new int[] { R.color.colorPrimary, R.color.colorAccent }, this);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // refresca el gráfico
    }
}