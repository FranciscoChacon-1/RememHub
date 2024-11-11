package sv.edu.catolica.rememhub;

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
        holder.textTitulo.setText(tarea.getNombre());
        holder.textCategoria.setText(tarea.getCategoria());
        holder.textFecha.setText(tarea.getFecha());

        // Configurar el clic para mostrar el diálogo de eliminar permanentemente
        holder.itemView.setOnClickListener(v -> mostrarDialogoEliminar(tarea));
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }



    private void mostrarDialogoEliminar(Tarea tarea) {
        // Crear el diálogo con la opción para eliminar permanentemente
        new AlertDialog.Builder(context)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea permanentemente?")
                .setPositiveButton("Eliminar permanentemente", (dialog, which) -> {
                    eliminarTareaPermanente(tarea); // Eliminar la tarea permanentemente
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarTareaPermanente(Tarea tarea) {
        // Eliminar la tarea de la base de datos permanentemente
        tareaDataAccess.eliminarTarea(tarea);
        listaTareas.remove(tarea); // Eliminarla de la lista visual
        notifyDataSetChanged(); // Notificar que la lista ha cambiado
        Toast.makeText(context, "Tarea eliminada permanentemente", Toast.LENGTH_SHORT).show(); // Confirmación
    }

    // Método para vaciar la papelera (se llama desde la actividad)
    public void vaciarPapelera() {
        for (Tarea tarea : listaTareas) {
            tareaDataAccess.eliminarTarea(tarea); // Eliminar tarea permanentemente de la base de datos
        }

        listaTareas.clear(); // Limpiar la lista de tareas de la papelera
        notifyDataSetChanged(); // Notificar el cambio en la interfaz

        Toast.makeText(context, "Papelera vacía", Toast.LENGTH_SHORT).show(); // Confirmación
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textCategoria, textFecha;

        public TareaViewHolder(View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textViewNombreTarea);
            textCategoria = itemView.findViewById(R.id.textViewCategoriaTarea);
            textFecha = itemView.findViewById(R.id.textViewFechaTarea);
        }
    }
}
