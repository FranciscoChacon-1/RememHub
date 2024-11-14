package sv.edu.catolica.rememhub;

import static sv.edu.catolica.rememhub.R.string.tarea_eliminada_permanentemente;
import static sv.edu.catolica.rememhub.R.string.tarea_restaurada;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class TareaPapeleraAdapter extends RecyclerView.Adapter<TareaPapeleraAdapter.TareaViewHolder> {

    private Context context;
    private List<Tarea> listaTareas;
    private TareaDataAccess tareaDataAccess;

    public TareaPapeleraAdapter(Context context, List<Tarea> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
        this.tareaDataAccess = new TareaDataAccess(context);
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea_papelera, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);

        holder.textTitulo.setText(tarea.getTitulo());
        holder.textCategoria.setText(tarea.getCategoria());
        holder.textFecha.setText(tarea.getFecha());

        holder.btnRestaurar.setOnClickListener(v -> {
            boolean isRestored = tareaDataAccess.restaurarTarea(tarea);

            if (isRestored) {
                listaTareas.remove(position);  // Eliminar de la lista
                notifyItemRemoved(position);   // Notificar eliminación del item
                notifyItemRangeChanged(position, listaTareas.size());  // Actualizar posiciones restantes
                Toast.makeText(context, R.string.tarea_restaurada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.error_al_restaurar, Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.eliminar_permanentemente)
                    .setMessage(R.string.seguro_que_quieres_eliminar_esta_tarea_permanentemente)
                    .setPositiveButton(R.string.si, (dialog, which) -> {
                        tareaDataAccess.eliminarTareaConRecordatorio(tarea.getId());
                        listaTareas.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaTareas.size()); // Actualizar posiciones restantes
                        Toast.makeText(context, R.string.tarea_eliminada_permanentemente, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textCategoria, textFecha;
        TextView btnRestaurar, btnEliminar;

        public TareaViewHolder(View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textViewNombreTarea);
            textCategoria = itemView.findViewById(R.id.textViewCategoriaTarea);
            textFecha = itemView.findViewById(R.id.textViewFechaTarea);
            btnRestaurar = itemView.findViewById(R.id.btnRestaurar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    // Método para vaciar visualmente la lista de tareas
    public void vaciarPapelera() {
        listaTareas.clear();
        notifyDataSetChanged();
    }

    // Método para actualizar la lista de tareas
    public void actualizarTareas(List<Tarea> nuevasTareas) {
        this.listaTareas = nuevasTareas;
        notifyDataSetChanged();
    }
}
