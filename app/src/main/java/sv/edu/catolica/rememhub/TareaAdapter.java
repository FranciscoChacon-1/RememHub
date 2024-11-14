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

    private final Context context;
    private final List<Tarea> listaTareas;
    private final TareaDataAccess tareaDataAccess;

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
        holder.bind(tarea, position);
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView textTitulo, textCategoria, textFecha, textDiasRecordatorio;
        private final Button btnEliminar;

        public TareaViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTarea);
            textTitulo = itemView.findViewById(R.id.textViewNombreTarea);
            textCategoria = itemView.findViewById(R.id.textViewCategoriaTarea);
            textFecha = itemView.findViewById(R.id.textViewFechaTarea);
            textDiasRecordatorio = itemView.findViewById(R.id.textViewDiasRecordatorio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(Tarea tarea, int position) {
            textTitulo.setText(tarea.getTitulo());
            textCategoria.setText(tarea.getCategoria() != null ? tarea.getCategoria() : context.getString(R.string.sin_categor_a));
            textFecha.setText(tarea.getFecha());
            checkBox.setChecked(tarea.isCompletada());
            checkBox.setEnabled(false);

            btnEliminar.setOnClickListener(v -> mostrarDialogoConfirmacion(tarea, position));
        }
    }

    // Muestra un cuadro de diálogo de confirmación antes de eliminar una tarea
    private void mostrarDialogoConfirmacion(Tarea tarea, int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirmaci_n)
                .setMessage(R.string.est_seguro_de_que_desea_mover_esta_tarea_a_la_papelera)
                .setPositiveButton(R.string.si, (dialog, which) -> moverTareaAPapelera(tarea, position))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // Mueve la tarea a la papelera en segundo plano
    @SuppressLint("StaticFieldLeak")
    private void moverTareaAPapelera(Tarea tarea, int position) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return tareaDataAccess.moverTareaAPapelera(tarea);
            }

            @Override
            protected void onPostExecute(Boolean isMoved) {
                if (isMoved) {
                    listaTareas.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, R.string.la_tarea_se_ha_movido_a_la_papelera, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.error_al_mover_la_tarea, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    // Actualiza la lista de tareas en el adaptador
    public void updateData(List<Tarea> newTasks) {
        listaTareas.clear();
        listaTareas.addAll(newTasks);
        notifyDataSetChanged();
    }
}
