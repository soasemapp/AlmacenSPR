package com.almacen.alamacen202.SetterandGetters;

public class UbicacionesAjuste {
    private String Ubicacione;
    private String Cantidad;
    private String Maximo;
    private String Observaciones;

    public UbicacionesAjuste(String ubicacione, String cantidad, String maximo, String observaciones) {
        Ubicacione = ubicacione;
        Cantidad = cantidad;
        Maximo = maximo;
        Observaciones = observaciones;
    }

    public String getUbicacione() {
        return Ubicacione;
    }

    public void setUbicacione(String ubicacione) {
        Ubicacione = ubicacione;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public String getMaximo() {
        return Maximo;
    }

    public void setMaximo(String maximo) {
        Maximo = maximo;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }
}//clase
