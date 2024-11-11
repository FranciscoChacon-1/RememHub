package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private Context context;
    private List<Tarea> listaTareas;
    private TareaDataAccess tareaDataAccess;

    public TareaAdapter(Context context, List<Tarea> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
        this.tareaDataAccess = new TareaDataAccess(context);
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);

        // Configurar los datos de la tarea en la interfaz
        holder.textTitulo.setText(tarea.getNombre());

        // Mostrar el nombre de la categoría de la tarea
        if (tarea.getCategoria() != null && !tarea.getCategoria().isEmpty()) {
            holder.textCategoria.setText(tarea.getCategoria());
        } else {
            holder.textCategoria.setText("Sin categoría"); // Texto por defecto si la categoría está vacía
        }

        // Mostrar la fecha de la tarea
        holder.textFecha.setText(tarea.getFecha());

        // Mostrar los días de recordatorio de la tarea
        holder.textDiasRecordatorio.setText(tarea.getDiasRecordatorio());

        // Mostrar el estado de completado
        holder.checkBox.setChecked(tarea.isCompletada());

        // Si la tarea está eliminada, ocultar el CheckBox; si no, mostrarlo y configurar su estado
        if (tarea.isEliminada()) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        // Configurar el listener para el CheckBox
        holder.checkBox.setOnClickListener(v -> {
            if (!tarea.isEliminada()) {
                mostrarDialogoConfirmacion(tarea, holder.checkBox, position);
            }
        });
    }

    private void mostrarDialogoConfirmacion(Tarea tarea, CheckBox checkBox, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro de que desea mover esta tarea a la papelera?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            moverTareaAPapelera(tarea, position);
            Toast.makeText(context, "La tarea se ha movido a la papelera", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> checkBox.setChecked(tarea.isCompletada()));
        builder.setOnCancelListener(dialog -> checkBox.setChecked(tarea.isCompletada()));
        builder.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void moverTareaAPapelera(final Tarea tarea, final int position) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                tareaDataAccess.marcarTareaComoEliminada(tarea);
                tarea.setEliminada(true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listaTareas.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "La tarea se ha movido a la papelera", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
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
            textDiasRecordatorio = itemView.findViewById(R.id.textViewDiasRecordatorio);
        }
    }

    // Método para actualizar la lista de tareas en el adaptador
    public void updateData(List<Tarea> newTasks) {
        listaTareas.clear();
        listaTareas.addAll(newTasks);
        notifyDataSetChanged();
    }
}
