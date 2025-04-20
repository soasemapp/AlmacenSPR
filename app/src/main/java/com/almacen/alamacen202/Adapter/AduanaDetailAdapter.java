package com.almacen.alamacen202.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.AduanaDetail;

import java.util.List;

public class AduanaDetailAdapter extends RecyclerView.Adapter<AduanaDetailAdapter.AduanaDetailViewHolder> {
    private List<AduanaDetail> listaDetalles;
    private Context context;

    public AduanaDetailAdapter(Context context, List<AduanaDetail> listaDetalles) {
        this.context = context;
        this.listaDetalles = listaDetalles;
    }

    @NonNull
    @Override
    public AduanaDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_aduana_detail, parent, false);
        return new AduanaDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AduanaDetailViewHolder holder, int position) {
        AduanaDetail detalle = listaDetalles.get(position);
        holder.textProducto.setText("Producto: " + detalle.getProducto());
        holder.textCantidad.setText("Cantidad: " + detalle.getCantidad());
        holder.textPrecio.setText("Precio: $" + detalle.getPrecio());
    }

    @Override
    public int getItemCount() {
        return listaDetalles.size();
    }

    public static class AduanaDetailViewHolder extends RecyclerView.ViewHolder {
        TextView textProducto, textCantidad, textPrecio;

        public AduanaDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textProducto = itemView.findViewById(R.id.textProducto);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            textPrecio = itemView.findViewById(R.id.textPrecio);
        }
    }
}
