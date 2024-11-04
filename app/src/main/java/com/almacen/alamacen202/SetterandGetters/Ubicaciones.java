package com.almacen.alamacen202.SetterandGetters;

public class Ubicaciones {
    private String ubicacion;
    private String existencia;
    private String cantsurt;

    public Ubicaciones(String ubicacion, String existencia, String cantsurt) {
        this.ubicacion = ubicacion;
        this.existencia = existencia;
        this.cantsurt = cantsurt;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getExistencia() {
        return existencia;
    }

    public void setExistencia(String existencia) {
        this.existencia = existencia;
    }

    public String getCantsurt() {
        return cantsurt;
    }

    public void setCantsurt(String cantsurt) {
        this.cantsurt = cantsurt;
    }
}//clase
