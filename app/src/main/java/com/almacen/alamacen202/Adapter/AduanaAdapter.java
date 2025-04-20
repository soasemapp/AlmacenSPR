package com.almacen.alamacen202.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Activity.AduanaDetailActivity;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Aduana;

import java.util.List;

public class AduanaAdapter extends RecyclerView.Adapter<AduanaAdapter.AduanaViewHolder> {
    private List<Aduana> listaAduanas;
    private Context context;
    private String codBra;

    public AduanaAdapter(Context context, List<Aduana> listaAduanas, String strcodBra) {
        this.context = context;
        this.listaAduanas = listaAduanas;
        this.codBra = codBra;

    }

    @NonNull
    @Override
    public AduanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_aduana, parent, false);
        return new AduanaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AduanaViewHolder holder, int position) {
        Aduana aduana = listaAduanas.get(position);
        holder.textCliente.setText("Cliente: " + aduana.getCliente());
        holder.textNombre.setText("Nombre: " + aduana.getNombre());
        holder.textFolio.setText("Folio: " + aduana.getFolio());
        holder.textFecha.setText("Fecha: " + aduana.getFecha());
        holder.textReferencia.setText("Referencia: " + aduana.getReferencia());
        holder.textDocumento.setText("Documento: " + aduana.getDocumento());
        holder.textCantidad.setText("Cantidad: " + aduana.getCantidad());
        holder.textCantidadSurt.setText("Cantidad Surtida: " + aduana.getCantidadSurt());
        holder.textUrgencia.setText("Urgencia: " + aduana.getUrgencia());

        // Manejar clic en el botón "Ver Detalles"
        holder.btnVerDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, AduanaDetailActivity.class);
            intent.putExtra("nombre", aduana.getNombre());
            intent.putExtra("folio", aduana.getFolio());
            intent.putExtra("sucursal", codBra);
            //intent.putExtra("sucursal", aduana.getCliente()); // Si la sucursal se almacena en cliente
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaAduanas.size();
    }

    public static class AduanaViewHolder extends RecyclerView.ViewHolder {
        TextView textCliente, textNombre, textFolio, textFecha, textReferencia, textDocumento, textCantidad, textCantidadSurt, textUrgencia;
        Button btnVerDetalles;

        public AduanaViewHolder(@NonNull View itemView) {
            super(itemView);
            textCliente = itemView.findViewById(R.id.textCliente);
            textNombre = itemView.findViewById(R.id.textNombre);
            textFolio = itemView.findViewById(R.id.textFolio);
            textFecha = itemView.findViewById(R.id.textFecha);
            textReferencia = itemView.findViewById(R.id.textReferencia);
            textDocumento = itemView.findViewById(R.id.textDocumento);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            textCantidadSurt = itemView.findViewById(R.id.textCantidadSurt);
            textUrgencia = itemView.findViewById(R.id.textUrgencia);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
