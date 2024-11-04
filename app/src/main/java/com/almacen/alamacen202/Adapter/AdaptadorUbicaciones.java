package com.almacen.alamacen202.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.CajaXProd;
import com.almacen.alamacen202.SetterandGetters.Ubicaciones;

import java.util.ArrayList;

public class AdaptadorUbicaciones extends RecyclerView.Adapter<AdaptadorUbicaciones.ViewHolderUbicaciones>{

    private ArrayList<Ubicaciones> listaubi;
    private int index;

    public AdaptadorUbicaciones(ArrayList<Ubicaciones> listaubi) {
        this.listaubi = listaubi;
    }

    @Override
    public AdaptadorUbicaciones.ViewHolderUbicaciones onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ubi_prod,
                null, false);
        return new AdaptadorUbicaciones.ViewHolderUbicaciones(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorUbicaciones.ViewHolderUbicaciones holder, int position) {
        holder.tvUbi.setText(listaubi.get(position).getUbicacion());
        holder.tvExi.setText(listaubi.get(position).getExistencia());
        holder.tvCan.setText(listaubi.get(position).getCantsurt());
    }

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
        return listaubi.size();
    }


    public class ViewHolderUbicaciones extends RecyclerView.ViewHolder {
        private  TextView tvUbi,tvExi,tvCan;
        public ViewHolderUbicaciones(View itemView) {
            super(itemView);
            tvUbi = itemView.findViewById(R.id.tvUbi);
            tvExi = itemView.findViewById(R.id.tvExi);
            tvCan = itemView.findViewById(R.id.tvCan);
        }
    }
}
