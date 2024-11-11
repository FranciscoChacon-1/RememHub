package sv.edu.catolica.rememhub;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private SQLiteDatabase db;
    private Context context;
    private List<Tarea> listaTareas;
    private TareaDataAccess tareaDataAccess;


    public TareaAdapter(Context context, List<Tarea> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
        this.tareaDataAccess = new TareaDataAccess(context);
        RememhubBD dbHelper = new RememhubBD(context);
        db = dbHelper.getReadableDatabase();
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);
        holder.textTitulo.setText(tarea.getNombre());
        holder.textCategoria.setText(tarea.getCategoria());
        holder.textFecha.setText(tarea.getFecha());

        // Mostrar los días en los que la tarea debe recordarse
        String diasRecordatorio = tarea.getDiasRecordatorio();  // Obtener los días de recordatorio
        holder.textDiasRecordatorio.setText(diasRecordatorio);  // Mostrarlo en el TextView

        // Mostrar el estado de la tarea eliminada
        if (tarea.isEliminada()) {
            holder.checkBox.setVisibility(View.GONE);  // Ocultar CheckBox si la tarea está eliminada
        } else {
            holder.checkBox.setChecked(tarea.isCompletada());
            holder.checkBox.setVisibility(View.VISIBLE);  // Mostrar CheckBox si la tarea no está eliminada
        }


        // Mostrar el AlertDialog al hacer clic en un item o marcar el CheckBox
        holder.itemView.setOnClickListener(v -> mostrarDialogoConfirmacion(tarea, holder.checkBox));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mostrarDialogoConfirmacion(tarea, holder.checkBox);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }


    private void mostrarDialogoConfirmacion(Tarea tarea, CheckBox checkBox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro de que desea mover esta tarea a la papelera?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            moverTareaAPapelera(tarea);
            Toast.makeText(context, "La tarea se ha movido a la papelera", Toast.LENGTH_SHORT).show(); // Mostrar el Toast
        });

        builder.setNegativeButton("No", (dialog, which) -> checkBox.setChecked(false));

        builder.setOnCancelListener(dialog -> checkBox.setChecked(false));

        builder.show();
    }

    private void moverTareaAPapelera(Tarea tarea) {
        // Aquí se mueve la tarea a la papelera sin redirigir a la actividad
        tareaDataAccess.marcarTareaComoEliminada(tarea);
        tarea.setEliminada(true);  // Marcar la tarea como eliminada


        // Eliminar la tarea de la lista y notificar al adaptador
        listaTareas.remove(tarea);
        notifyDataSetChanged();
    }


    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textTitulo, textCategoria, textFecha, textDiasRecordatorio;

        public TareaViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTarea);
            textTitulo = itemView.findViewById(R.id.textViewNombreTarea);
            textCategoria = itemView.findViewById(R.id.textViewCategoriaTarea);
            textFecha = itemView.findViewById(R.id.textViewFechaTarea);
            textDiasRecordatorio = itemView.findViewById(R.id.textViewDiasRecordatorio);  // Agregar el nuevo TextView para los días
        }
    }

    public List<Tarea> obtenerTareasNoCompletadas() {
        List<Tarea> listaTareasNoCompletadas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.estado = 0";  // 0: no completada

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareasNoCompletadas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasNoCompletadas;
    }


}
