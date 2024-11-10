package sv.edu.catolica.rememhub.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sv.edu.catolica.rememhub.Categorias;
import sv.edu.catolica.rememhub.R;
import sv.edu.catolica.rememhub.VerActivity;

public class ListaCategoriasAdapter extends RecyclerView.Adapter<ListaCategoriasAdapter.categoriaViewHolder> {

    ArrayList<Categorias>ListaCategorias;
    public ListaCategoriasAdapter(ArrayList<Categorias> ListaCategorias){
        this.ListaCategorias = ListaCategorias;

    }


    @NonNull
    @Override
    public categoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_categoria,null,false);
        return  new categoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoriaViewHolder holder, int position) {
        holder.viewNombre.setText(ListaCategorias.get(position).getNombre());

    }

    @Override
    public int getItemCount() {
        return ListaCategorias.size();

    }

    public class categoriaViewHolder extends RecyclerView.ViewHolder {
        TextView viewNombre;

        public categoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            viewNombre= itemView.findViewById(R.id.textCat);

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, VerActivity.class);
                    intent.putExtra("ID", ListaCategorias.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });


        }
    }
}
