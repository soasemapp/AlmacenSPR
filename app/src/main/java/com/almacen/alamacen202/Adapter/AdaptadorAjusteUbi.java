package com.almacen.alamacen202.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.SetterandGetters.UbicacionesAjuste;

import java.util.ArrayList;

public class AdaptadorAjusteUbi extends RecyclerView.Adapter<AdaptadorAjusteUbi.ViewHolderUbicacionesAjuste> {

    private ArrayList<UbicacionesAjuste> datos;
    private int index;
    public AdaptadorAjusteUbi(ArrayList<UbicacionesAjuste> datos) {
        this.datos = datos;
    }//constructor

    @Override
    public AdaptadorAjusteUbi.ViewHolderUbicacionesAjuste onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ubi_ajust,
                null, false);
        return new AdaptadorAjusteUbi.ViewHolderUbicacionesAjuste(view);
    }//oncreateViewHolder

    @Override
    public void onBindViewHolder(AdaptadorAjusteUbi.ViewHolderUbicacionesAjuste holder, int position) {
        holder.ubi.setText(datos.get(position).getUbicacione());
        holder.Cantidad.setText(datos.get(position).getCantidad());
        holder.tvObs.setText(datos.get(position).getObservaciones());

        if(index==position){
            holder.lyUbA.setBackgroundResource(R.color.colorSelec);//seleccion
        }else{
            holder.lyUbA.setBackgroundColor(0);
        }//else no esta seleccionado
    }//onBindViewHolder

    public int index(int index){
        this.index=index;
        return index;
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }//getItemType

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolderUbicacionesAjuste extends RecyclerView.ViewHolder {
        TextView ubi,Cantidad,tvObs;
        LinearLayout lyUbA;
        public ViewHolderUbicacionesAjuste (View itemView) {
            super(itemView);
            ubi= itemView.findViewById(R.id.ubi);
            Cantidad =  itemView.findViewById(R.id.Cantidad);
            tvObs =  itemView.findViewById(R.id.tvObs);
            lyUbA = itemView.findViewById(R.id.lyUbA);
            ubi = itemView.findViewById(R.id.ubi);
        }//constructor
    }//AdapterUbicacionesAjusteViewHolder class
}//principal
