package com.almacen.alamacen202.XML;



import com.almacen.alamacen202.SetterandGetters.AduanaProductoDetail;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class XMLAduana  extends SoapSerializationEnvelope {
    String Comentario1="",Comentario2="",Comentario3="";
    String ClaveVendedor = "";
    String NombreCliente = "";
    String ClaveCliente = "";
    String FechaActual = "";
    String FechaVencimiento = "";
    String Sucursal = "";
    String usuario = "";
    String clave = "";
    String rfcCliente = "";
    String plazo = "";
    String Montototal = "";
    String Iva = "";
    String Descuento = "";
    String DescuentoPro = "";
    String Desc1 = "";
    String Calle = "";
    String Colonia = "";
    String Poblacion = "";
    String FolioPrev="";
    String Via="";
    String dirEnv="";
    String DescuentoStr="";
    ArrayList<AduanaProductoDetail> listaAduanaproductos = new ArrayList<>();


    String Server;
    String Eagle;
    String Rodatech;
    String Partech;
    String Shark;
    String Trackoone;
    String DESCdOCUMENTO;




    public XMLAduana(int version) {
        super(version);
    }


    public void XMLAduana(  String claveVendedor, String nombreCliente, String claveCliente, String fechaActual, String fechaVencimiento,
                           String sucursal, String usuario, String clave, String rfcCliente, String plazo, String montototal, String iva, String descuento, String descuentoPro,
                           String Desc1,String calle, String colonia, String poblacion, String folioPrev,String Via,String dirEnv,ArrayList<AduanaProductoDetail> listaAduanaproductos,
                           String StrServer,String eagle, String rodatech, String partech, String shark, String trackoone, String DESCdOCUMENTO) {

        this.ClaveVendedor = claveVendedor;
        this.NombreCliente = nombreCliente;
        this.ClaveCliente = claveCliente;
        this.FechaActual = fechaActual;
        this.FechaVencimiento = fechaVencimiento;
        this.Sucursal = sucursal;
        this.usuario = usuario;
        this.clave = clave;
        this.rfcCliente = rfcCliente;
        this.plazo = plazo;
        this.Montototal = montototal;
        this.Iva = iva;
        this.Descuento = descuento;
        this.DescuentoPro = descuentoPro;
        this.Desc1 = Desc1;
        this.Calle = calle;
        this.Colonia = colonia;
        this.Poblacion = poblacion;
        this.FolioPrev = folioPrev;
        this.Via = Via;
        this.dirEnv = dirEnv;
        this.listaAduanaproductos = listaAduanaproductos;
        this.Server = StrServer;
        this.Eagle = eagle;
        this.Rodatech = rodatech;
        this.Partech = partech;
        this.Shark = shark;
        this.Trackoone = trackoone;
        this.DESCdOCUMENTO = DESCdOCUMENTO;


    }

    private static String formatNumberCurrency(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(Double.parseDouble(number));
    }
    @Override
    public void write(XmlSerializer writer) throws IOException {

        env = "http://schemas.xmlsoap.org/soap/envelope/";
        String tem = "";
        writer.startDocument("UTF-8", true);
        writer.setPrefix("soap", env);

        writer.setPrefix("", tem);

        writer.startTag(env, "Envelope");

        writer.startTag(env, "Body");

        writer.startTag(tem, "newDocRequest");

        writer.startTag(tem, "Login");

        writer.startTag(tem, "user");
        writer.text(usuario);
        writer.endTag(tem, "user");

        writer.startTag(tem, "pass");
        writer.text(clave);
        writer.endTag(tem, "pass");

        writer.endTag(tem, "Login");

        writer.startTag(tem, "Doc");

        writer.startTag(tem, "k_sucursal");
        writer.text(Sucursal);
        writer.endTag(tem, "k_sucursal");

        writer.startTag(tem, "k_almacen");
        writer.text("1");
        writer.endTag(tem, "k_almacen");

        writer.startTag(tem, "k_genero");
        writer.text("U");
        writer.endTag(tem, "k_genero");

        writer.startTag(tem, "k_naturaleza");
        writer.text("D");
        writer.endTag(tem, "k_naturaleza");

        writer.startTag(tem, "k_grupo");
        writer.text("45");
        writer.endTag(tem, "k_grupo");

        writer.startTag(tem, "k_tipo");
        writer.text("1");
        writer.endTag(tem, "k_tipo");

        writer.startTag(tem, "k_fecha");
        writer.text(FechaActual);
        writer.endTag(tem, "k_fecha");

        writer.startTag(tem, "k_clave");
        writer.text(ClaveCliente);
        writer.endTag(tem, "k_clave");

        writer.startTag(tem, "k_rfc");
        writer.text(rfcCliente);
        writer.endTag(tem, "k_rfc");

        writer.startTag(tem, "k_nombre");
        writer.text(NombreCliente);
        writer.endTag(tem, "k_nombre");


        writer.startTag(tem, "k_calle");
        writer.text(Calle);
        writer.endTag(tem, "k_calle");


        writer.startTag(tem, "k_colonia");
        writer.text(Colonia);
        writer.endTag(tem, "k_colonia");


        writer.startTag(tem, "k_poblacion");
        writer.text(Poblacion);
        writer.endTag(tem, "k_poblacion");

        writer.startTag(tem, "k_desc1");
        writer.text(Desc1);
        writer.endTag(tem, "k_desc1");

        writer.startTag(tem, "k_agente");
        writer.text(ClaveVendedor);
        writer.endTag(tem, "k_agente");

        writer.startTag(tem, "k_moneda");
        writer.text("PESOS");
        writer.endTag(tem, "k_moneda");

        writer.startTag(tem, "k_paridad");
        writer.text("1");
        writer.endTag(tem, "k_paridad");

        writer.startTag(tem, "k_refer");
        writer.text("");
        writer.endTag(tem, "k_refer");

        writer.startTag(tem, "k_plazo");
        writer.text(plazo);
        writer.endTag(tem, "k_plazo");

        writer.startTag(tem, "k_vence");
        writer.text(FechaVencimiento);
        writer.endTag(tem, "k_vence");

        writer.startTag(tem, "k_cond");
        writer.text("");
        writer.endTag(tem, "k_cond");



        writer.startTag(tem, "k_coment");
        writer.text(Comentario1);
        writer.endTag(tem, "k_coment");



        writer.startTag(tem, "k_desc");
        writer.text(formatNumberCurrency(DescuentoStr));
        writer.endTag(tem, "k_desc");


        writer.startTag(tem, "k_87");
        writer.text(DescuentoPro);
        writer.endTag(tem, "k_87");

        writer.startTag(tem, "k_39");
        writer.text(FolioPrev);
        writer.endTag(tem, "k_39");


        writer.startTag(tem, "k_38");
        writer.text("1");
        writer.endTag(tem, "k_38");


        writer.startTag(tem, "k_37");
        writer.text("43");
        writer.endTag(tem, "k_37");



        writer.startTag(tem, "k_36");
        writer.text("D");
        writer.endTag(tem, "k_36");



        writer.startTag(tem, "k_iva");
        writer.text(formatNumberCurrency(Iva));
        writer.endTag(tem, "k_iva");


        writer.startTag(tem, "k_monto");
        writer.text(formatNumberCurrency(Montototal));
        writer.endTag(tem, "k_monto");

        writer.startTag(tem, "k_70");
        writer.text(Via);
        writer.endTag(tem, "k_70");



        writer.startTag(tem, "k_74");
        writer.text(dirEnv);
        writer.endTag(tem, "k_74");


        writer.startTag(tem, "k_81");
        writer.text("V");
        writer.endTag(tem, "k_81");

        writer.startTag(tem, "k_63");
        writer.text("UD4501-");
        writer.endTag(tem, "k_63");


        writer.startTag(tem, "k_97");
        writer.text(Eagle);         //Eagle
        writer.endTag(tem, "k_97");

        writer.startTag(tem, "k_98");
        writer.text(Trackoone);         //Track
        writer.endTag(tem, "k_98");

        writer.startTag(tem, "k_99");
        writer.text(Rodatech);         //Rodatech
        writer.endTag(tem, "k_99");

        writer.startTag(tem, "k_100");
        writer.text(Partech);         //Partech
        writer.endTag(tem, "k_100");

        writer.startTag(tem, "k_101");
        writer.text(Shark);         //Shark
        writer.endTag(tem, "k_101");

        writer.startTag(tem, "k_102");
        writer.text(DESCdOCUMENTO);         //Descuento documento
        writer.endTag(tem, "k_102");

        writer.startTag(tem, "k_25");
        writer.text(Comentario2);
        writer.endTag(tem, "k_25");

        writer.startTag(tem, "k_26");
        writer.text(Comentario3);
        writer.endTag(tem, "k_26");



        /*Items*/
        writer.startTag(tem, "k_items");
        for (int i = 0; i < listaAduanaproductos.size(); i++) {

            writer.startTag(tem, "item");

            writer.startTag(tem, "k_parte");
            writer.text(listaAduanaproductos.get(i).getSku());
            writer.endTag(tem, "k_parte");

            writer.startTag(tem, "k_Q");
            writer.text(listaAduanaproductos.get(i).getSurtido());
            writer.endTag(tem, "k_Q");

          /*  writer.startTag(tem, "k_desc1");
            writer.text(listaAduanaproductos.get(i).getDesc1());
            writer.endTag(tem, "k_desc1"); */

            writer.startTag(tem, "k_descr");
            writer.text(listaAduanaproductos.get(i).getDescripcion());
            writer.endTag(tem, "k_descr");

            writer.startTag(tem, "k_unidad");
            writer.text(listaAduanaproductos.get(i).getUnidad());
            writer.endTag(tem, "k_unidad");

            writer.startTag(tem, "k_precio");
            writer.text(formatNumberCurrency(listaAduanaproductos.get(i).getPrecio()));
            writer.endTag(tem, "k_precio");



            writer.startTag(tem, "k_monto");
            writer.text(formatNumberCurrency(listaAduanaproductos.get(i).getImporte()));
            writer.endTag(tem, "k_monto");



            writer.startTag(tem, "k_iva");
            writer.text(((!Server.equals("vazlocolombia.dyndns.org:9085")?"16" : "19")));
            writer.endTag(tem, "k_iva");

            writer.startTag(tem, "k_ieps");
            writer.text("0");
            writer.endTag(tem, "k_ieps");

            writer.startTag(tem, "k_sucursal");
            writer.text(Sucursal);
            writer.endTag(tem, "k_sucursal");

            writer.startTag(tem, "k_genero");
            writer.text("U");
            writer.endTag(tem, "k_genero");

            writer.startTag(tem, "k_natPrv");
            writer.text("D");
            writer.endTag(tem, "k_natPrv");

            writer.startTag(tem, "k_grpPrv");
            writer.text("43");
            writer.endTag(tem, "k_grpPrv");

            writer.startTag(tem, "k_tipPrv");
            writer.text("1");
            writer.endTag(tem, "k_tipPrv");

            writer.startTag(tem, "k_folPrv");
            writer.text(FolioPrev);
            writer.endTag(tem, "k_folPrv");

            writer.startTag(tem, "k_parPrv");
            writer.text(listaAduanaproductos.get(i).getPartida());
            writer.endTag(tem, "k_parPrv");

            writer.startTag(tem, "k_QO");
            writer.text(listaAduanaproductos.get(i).getPedido());
            writer.endTag(tem, "k_QO");


            writer.endTag(tem, "item");

        }
        writer.endTag(tem, "k_items");
        writer.endTag(tem, "Doc");

        writer.endTag(tem, "newDocRequest");
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
        writer.endDocument();

    }

}
