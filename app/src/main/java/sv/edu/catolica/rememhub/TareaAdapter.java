package sv.edu.catolica.rememhub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private List<Tarea> listaTareas;
    private Context context;
    public TareaAdapter(Context context, List<Tarea> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);
        holder.checkBoxTarea.setChecked(tarea.isCompletada());
        holder.textViewNombreTarea.setText(tarea.getNombre());
        holder.textViewCategoriaTarea.setText(tarea.getCategoria());
        holder.textViewFechaTarea.setText(tarea.getFecha());

        holder.checkBoxTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new AlertDialog.Builder(context)
                        .setTitle("Finalizar tarea")
                        .setMessage("¿Finalizar tarea?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            tarea.setCompletada(true);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            holder.checkBoxTarea.setChecked(false); //
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxTarea;
        TextView textViewNombreTarea;
        TextView textViewCategoriaTarea;
        TextView textViewFechaTarea;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTarea = itemView.findViewById(R.id.checkBoxTarea);
            textViewNombreTarea = itemView.findViewById(R.id.textViewNombreTarea);
            textViewCategoriaTarea = itemView.findViewById(R.id.textViewCategoriaTarea);
            textViewFechaTarea = itemView.findViewById(R.id.textViewFechaTarea);
        }
    }
}
