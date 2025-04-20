package com.almacen.alamacen202.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AduanaDetailAdapter;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.AduanaDetail;
import com.almacen.alamacen202.SetterandGetters.AduanaParte1;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLAduana;
import com.almacen.alamacen202.includes.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.almacen.alamacen202.Adapter.AduanaProductoAdapter;
import com.almacen.alamacen202.SetterandGetters.AduanaProductoDetail;

public class AduanaDetailActivity extends AppCompatActivity {
    private static final String TAG = "AduanaDetailActivity";
    private RecyclerView recyclerView;
    private AduanaDetailAdapter adapter;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private String strusr, strpass, StrServer, sucursal, nombre, folio;
    private TextView textNombre, textFolio, textSucursal;
    private Button btnGenerarDocumento;

    private RecyclerView recyclerViewDetalleProducto;
    private AduanaProductoAdapter detalleProductoAdapter;
    private List<AduanaProductoDetail> listaDetalleProductos = new ArrayList<>();
    private AduanaParte1 parte1Info;
    private ConexionSQLiteHelper conn;
    private Button ButtonCot;
    private ProgressDialog mDialog;
    private String Mensaje = "", Documento = "", Folio = "", mensaje = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aduana_detail);

        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();

        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        StrServer = preference.getString("Server", "null");
        String strcodBra = preference.getString("codBra", "null");



        nombre = getIntent().getStringExtra("nombre");
        folio = getIntent().getStringExtra("folio");
        sucursal = getIntent().getStringExtra("sucursal");

        textNombre = findViewById(R.id.textNombre);
        textFolio = findViewById(R.id.textFolio);
        textSucursal = findViewById(R.id.textSucursal);

        textNombre.setText("Nombre: " + nombre);
        textFolio.setText("Folio: " + folio);
        textSucursal.setText("Sucursal: " + strcodBra);

        //recyclerView = findViewById(R.id.recyclerViewDetalles);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewDetalleProducto = findViewById(R.id.recyclerViewDetalleProducto);
        recyclerViewDetalleProducto.setLayoutManager(new LinearLayoutManager(this));



        btnGenerarDocumento = findViewById(R.id.btnGenerarDocumento);


        btnGenerarDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AduanaDetailActivity.AsyncCallWS task = new AduanaDetailActivity.AsyncCallWS();
                task.execute();
            }
        });


        new ObtenerDetallesTask().execute(StrServer, strcodBra, folio);

        //new ObtenerDetallesTask().execute(StrServer, sucursal, nombre, folio);
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            conectar1();
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {

            if (!Documento.equals("") && !Folio.equals("")) {
                mDialog.dismiss();
                AlertDialog.Builder alerta = new AlertDialog.Builder(AduanaDetailActivity.this);
                alerta.setMessage("Documento = " + Documento + "\n" +
                        "Folio = " + Folio).setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        conn = new ConexionSQLiteHelper(AduanaDetailActivity.this, "bd_Carrito", null, 1);

                        editor.clear();
                        editor.commit();
                        //BorrarCarrito();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        finish();

                        dialogInterface.cancel();

                    }


                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle(Mensaje);
                titulo.show();

                ButtonCot.setEnabled(true);

            } else {
                AlertDialog.Builder alerta = new AlertDialog.Builder(AduanaDetailActivity.this);
                alerta.setMessage("Hubo un problema con la conexion verifique su conexion eh intentelo nuevamente").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });

                AlertDialog titulo = alerta.create();
                titulo.setTitle("Problemas");
                titulo.show();

                ButtonCot.setEnabled(true);

            }


        }


    }

    private void conectar1() {
        String SOAP_ACTION = "NewDoc";
        String METHOD_NAME = "NewDoc";
        String NAMESPACE = "http://" + StrServer + "/WSk80Docs/";
        String URL = "http://" + StrServer + "/WSk80Docs";


        try {
            ArrayList<AduanaProductoDetail> listaAduanaproductos = new ArrayList<>();
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

        /*
            StringBuilder partida = new StringBuilder();
            StringBuilder sku = new StringBuilder();
            StringBuilder descripcion = new StringBuilder();
            StringBuilder pedido = new StringBuilder();
            StringBuilder unidad = new StringBuilder();
            StringBuilder precio = new StringBuilder();
            StringBuilder importe = new StringBuilder();
            StringBuilder surtido = new StringBuilder();

            for (int i = 0; i < listaDetalleProductos.size(); i++) {
                AduanaProductoDetail producto = listaDetalleProductos.get(i);
                partida.append(i + 1).append(",");
                sku.append(producto.getSku()).append(",");
                descripcion.append(producto.getDescripcion()).append(",");
                pedido.append("1").append(",");
                unidad.append("PZA").append(",");
                precio.append(producto.getPrecio()).append(",");
                importe.append(producto.getImporte()).append(",");
                surtido.append(producto.getSurtido()).append(",");
            }


            String strPartida = partida.substring(0, partida.length() - 1);
            String strSku = sku.substring(0, sku.length() - 1);
            String strDescripcion = descripcion.substring(0, descripcion.length() - 1);
            String strPedido = pedido.substring(0, pedido.length() - 1);
            String strUnidad = unidad.substring(0, unidad.length() - 1);
            String strPrecio = precio.substring(0, precio.length() - 1);
            String strImporte = importe.substring(0, importe.length() - 1);
            String strSurtido = surtido.substring(0, surtido.length() - 1);
*/

            XMLAduana soapEnvelope = new XMLAduana(SoapEnvelope.VER11);
            soapEnvelope.XMLAduana(
                    parte1Info.getVendedor(),
                    parte1Info.getNombre(),
                    parte1Info.getCliente(),
                    parte1Info.getFecha(),
                    parte1Info.getVencimiento(),
                    parte1Info.getSucursal(),
                    strusr,
                    strpass,
                    parte1Info.getRfc(),
                    parte1Info.getPlazo(),
                    parte1Info.getMonto(),
                    parte1Info.getIva(),
                    parte1Info.getDes1(),
                    parte1Info.getDespron(),
                    parte1Info.getDesc1(),
                    parte1Info.getCalle(),
                    parte1Info.getColonia(),
                    parte1Info.getPoblacion(),
                    parte1Info.getFolprev(),
                    parte1Info.getVia(),
                    parte1Info.getDirenv(),
                    listaAduanaproductos,
                    StrServer,
                    parte1Info.getDescE(),
                    parte1Info.getDescR(),
                    parte1Info.getDescP(),
                    parte1Info.getDescS(),
                    parte1Info.getDescT(),
                    parte1Info.getDescD()

            );

            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);

            SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;

            Mensaje = (response0.getPropertyAsString("message").equals("anyType{}") ? "" : response0.getPropertyAsString("message"));
            Documento = (response0.getPropertyAsString("doc").equals("anyType{}") ? "" : response0.getPropertyAsString("doc"));
            Folio = (response0.getPropertyAsString("folio").equals("anyType{}") ? "" : response0.getPropertyAsString("folio"));

        } catch (SoapFault | XmlPullParserException soapFault) {
            mDialog.dismiss();
            mensaje = "Error: " + soapFault.getMessage();
            soapFault.printStackTrace();
        } catch (IOException e) {
            mDialog.dismiss();
            mensaje = "No se encontró el servidor";
            e.printStackTrace();
        } catch (Exception ex) {
            mDialog.dismiss();
            mensaje = "Error: " + ex.getMessage();
        }
    }

    private class ObtenerDetallesTask extends AsyncTask<String, Void, List<AduanaProductoDetail>>{

        @Override
        protected List<AduanaProductoDetail> doInBackground(String... params) {
            String server = params[0];
            String sucursal = params[1];
            String folio = params[2];


            String urlString = "http://" + server + "/aduana?folio=" + folio + "&sucursal=" + sucursal;
            HttpHandler httpHandler = new HttpHandler();
            String jsonResponse = httpHandler.makeServiceCall(urlString, strusr, strpass);

            List<AduanaProductoDetail> productos = new ArrayList<>();


            try {
                if (jsonResponse != null) {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONObject liberacion = jsonObject.getJSONObject("Liberacion");
                    JSONObject parte1 = liberacion.getJSONObject("Parte1");
                    JSONObject parte2 = liberacion.getJSONObject("Parte2");


                    JSONArray keys = parte2.names();
                    for (int i = 0; i < keys.length(); i++) {
                        String key = keys.getString(i);
                        JSONObject item = parte2.getJSONObject(key);

                        AduanaProductoDetail producto = new AduanaProductoDetail();
                        producto.setSku(item.getString("sku"));
                        producto.setDescripcion(item.getString("desc"));
                        producto.setPrecio(item.getString("precio"));
                        producto.setImporte(item.getString("importe"));
                        producto.setSurtido(item.getString("surtido"));
                        producto.setSurtido(item.getString("partida"));
                        producto.setSurtido(item.getString("pedido"));

                        parte1Info = new AduanaParte1();
                        parte1Info.setSucursal(parte1.getString("sucursal"));
                        parte1Info.setFecha(parte1.getString("fecha"));
                        parte1Info.setCliente(parte1.getString("cliente"));
                        parte1Info.setNombre(parte1.getString("nombre"));
                        parte1Info.setVendedor(parte1.getString("vendedor"));
                        parte1Info.setMonto(parte1.getString("monto"));


                        productos.add(producto);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error al procesar Parte2: ", e);
            }

            return productos;
        }
        @Override
        protected void onPostExecute(List<AduanaProductoDetail> detalles) {
            if (detalles != null && !detalles.isEmpty()) {
                detalleProductoAdapter = new AduanaProductoAdapter(AduanaDetailActivity.this, detalles);
                recyclerViewDetalleProducto.setAdapter(detalleProductoAdapter);
            } else {
                Toast.makeText(AduanaDetailActivity.this, "No se encontraron detalles del producto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
