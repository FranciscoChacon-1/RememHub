package sv.edu.catolica.rememhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private Context context;
    private List<Tarea> tareas;

    public TareaAdapter(Context context, List<Tarea> tareas) {
        this.context = context;
        this.tareas = tareas;
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Asegúrate de que la ruta del layout sea correcta
        View view = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);
        holder.textViewNombreTarea.setText(tarea.getNombre());
        holder.textViewCategoriaTarea.setText(tarea.getCategoria());
        holder.textViewFechaTarea.setText(tarea.getFecha());

        // Si la tarea está completada, actualiza el estado del CheckBox
        holder.checkBoxTarea.setChecked(tarea.isCompletada());

        // Acción cuando se marca o desmarca el CheckBox
        holder.checkBoxTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tarea.setCompletada(isChecked);
            // Aquí puedes agregar lógica para actualizar la tarea en la base de datos
            // tareaDataAccess.marcarTareaComoCompletada(tarea);  // Esto si tienes acceso a la base de datos
        });
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNombreTarea;
        TextView textViewCategoriaTarea;
        TextView textViewFechaTarea;
        CheckBox checkBoxTarea;

        public TareaViewHolder(View itemView) {
            super(itemView);
            textViewNombreTarea = itemView.findViewById(R.id.textViewNombreTarea);
            textViewCategoriaTarea = itemView.findViewById(R.id.textViewCategoriaTarea);
            textViewFechaTarea = itemView.findViewById(R.id.textViewFechaTarea);
            checkBoxTarea = itemView.findViewById(R.id.checkBoxTarea);
        }
    }
}
