package com.almacen.alamacen202.SetterandGetters;


public class Aduana {
    private String cliente;
    private String nombre;
    private String folio;
    private String fecha;
    private String referencia;
    private String documento;
    private int cantidad;
    private int cantidadSurt;
    private String urgencia;

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getCantidadSurt() { return cantidadSurt; }
    public void setCantidadSurt(int cantidadSurt) { this.cantidadSurt = cantidadSurt; }

    public String getUrgencia() { return urgencia; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }
}