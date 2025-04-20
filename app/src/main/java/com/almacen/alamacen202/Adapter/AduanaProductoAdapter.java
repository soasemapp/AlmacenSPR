package com.almacen.alamacen202.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.AduanaProductoDetail;

import java.util.List;

public class AduanaProductoAdapter extends RecyclerView.Adapter<AduanaProductoAdapter.ProductoViewHolder> {

    private Context context;
    private List<AduanaProductoDetail> listaProductos;

    public AduanaProductoAdapter(Context context, List<AduanaProductoDetail> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_aduana_productos, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        AduanaProductoDetail producto = listaProductos.get(position);
        holder.textSku.setText( producto.getSku());
        holder.textDescripcion.setText( producto.getDescripcion());
        holder.textPartida.setText( producto.getPartida());
        holder.textPedido.setText( producto.getPedido());
        holder.textSurtido.setText( producto.getSurtido());
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textSku, textDescripcion, textPedido, textPartida, textSurtido;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textSku = itemView.findViewById(R.id.textSku);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textPedido = itemView.findViewById(R.id.textPedido);
            textPartida = itemView.findViewById(R.id.textPartida);
            textSurtido = itemView.findViewById(R.id.textSurtido);
        }
    }
}