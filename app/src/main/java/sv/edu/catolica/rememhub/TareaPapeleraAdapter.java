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

        // Configuración de los textos de los elementos de la tarea
        holder.textTitulo.setText(tarea.getTitulo());
        holder.textCategoria.setText(tarea.getCategoria());
        holder.textFecha.setText(tarea.getFecha());

        // Acción para restaurar la tarea
        holder.btnRestaurar.setOnClickListener(v -> {
            tareaDataAccess.restaurarTarea(tarea);  // Cambia columna papelera a 0 en la BD
            listaTareas.remove(position);           // Elimina la tarea de la lista visual
            notifyItemRemoved(position);            // Notifica el cambio visual
            Toast.makeText(context, tarea_restaurada, Toast.LENGTH_SHORT).show();
        });

        // Acción para eliminar permanentemente la tarea
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar permanentemente")
                    .setMessage("¿Seguro que quieres eliminar esta tarea permanentemente?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        tareaDataAccess.eliminarTareaConRecordatorio(tarea.getId());  // Elimina de la BD y los recordatorios
                        listaTareas.remove(position);                    // Remueve de la lista visual
                        notifyItemRemoved(position);                     // Notifica el cambio
                        Toast.makeText(context, tarea_eliminada_permanentemente, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
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

    // Método para eliminar todas las tareas de la papelera
    public void vaciarPapelera() {
        // Llama al método en TareaDataAccess para eliminar todas las tareas en papelera y sus recordatorios
        tareaDataAccess.eliminarTareasPapelera();
        listaTareas.clear(); // Elimina todas las tareas de la lista visual
        notifyDataSetChanged(); // Notifica que la lista ha cambiado
    }
}
