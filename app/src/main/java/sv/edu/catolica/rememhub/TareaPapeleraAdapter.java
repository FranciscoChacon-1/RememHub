package sv.edu.catolica.rememhub;

import android.content.Context;
import android.widget.CheckBox;
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

    public void TareaAdapterPapelera(Context context, List<Tarea> listaTareas) {
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



    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }



    private void moverTareaAPapelera(Tarea tarea) {
        // Aquí se mueve la tarea a la papelera sin redirigir a la actividad
        tareaDataAccess.marcarTareaComoEliminada(tarea); // Marcar la tarea como eliminada en la base de datos
        listaTareas.remove(tarea); // Eliminarla de la lista visual
        notifyDataSetChanged(); // Notificar que la lista ha cambiado para actualizar la interfaz
    }

    // Método para vaciar la papelera (se llama desde la actividad)
    public void vaciarPapelera() {
        for (Tarea tarea : listaTareas) {
            tareaDataAccess.eliminarTarea(tarea); // Eliminar tarea de la base de datos
        }

        listaTareas.clear(); // Limpiar la lista de tareas de la papelera
        notifyDataSetChanged(); // Notificar el cambio en la interfaz

        Toast.makeText(context, "Papelera vacía", Toast.LENGTH_SHORT).show(); // Mostrar el mensaje de confirmación
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

