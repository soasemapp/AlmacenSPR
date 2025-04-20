package com.almacen.alamacen202.SetterandGetters;



public class AduanaProductoDetail {
    public String partida;
    public String sku;
    public String descripcion;
    public String pedido;
    public String unidad;
    public String precio;
    public String importe;
    public String surtido;


    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }


    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getPrecio() {
        return precio;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getSurtido() {
        return surtido;
    }

    public void setSurtido(String surtido) {
        this.surtido = surtido;
    }



}