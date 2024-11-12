package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        holder.textTitulo.setText(tarea.getTitulo());
        holder.textCategoria.setText(tarea.getCategoria() != null ? tarea.getCategoria() : "Sin categoría");
        holder.textFecha.setText(tarea.getFecha());
        holder.checkBox.setChecked(tarea.isCompletada()); // Chequear según el estado
        holder.checkBox.setEnabled(false);  // Desactivar el cambio del checkbox

        // Configurar el botón de eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(tarea, holder.checkBox, position);
        });
    }

    // Método para mostrar el cuadro de diálogo de confirmación al eliminar una tarea
    private void mostrarDialogoConfirmacion(Tarea tarea, CheckBox checkBox, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro de que desea mover esta tarea a la papelera?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            moverTareaAPapelera(tarea, position);
        });

        builder.setNegativeButton("No", (dialog, which) -> checkBox.setChecked(tarea.isCompletada()));
        builder.setOnCancelListener(dialog -> checkBox.setChecked(tarea.isCompletada()));
        builder.show();
    }

    // Método para mover la tarea a la papelera de forma asincrónica
    @SuppressLint("StaticFieldLeak")
    private void moverTareaAPapelera(final Tarea tarea, final int position) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Llamar al método de TareaDataAccess para mover la tarea
                tareaDataAccess.moverTareaAPapelera(tarea);
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
        Button btnEliminar; // Declaración del botón de eliminar

        public TareaViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTarea);
            textTitulo = itemView.findViewById(R.id.textViewNombreTarea);
            textCategoria = itemView.findViewById(R.id.textViewCategoriaTarea);
            textFecha = itemView.findViewById(R.id.textViewFechaTarea);
            textDiasRecordatorio = itemView.findViewById(R.id.textViewDiasRecordatorio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar); // Inicialización del botón de eliminar
        }
    }

    // Método para actualizar la lista de tareas en el adaptador
    public void updateData(List<Tarea> newTasks) {
        listaTareas.clear();
        listaTareas.addAll(newTasks);
        notifyDataSetChanged();
    }
}
