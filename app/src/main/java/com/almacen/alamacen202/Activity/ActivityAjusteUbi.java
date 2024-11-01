package com.almacen.alamacen202.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorAjusteUbi;
import com.almacen.alamacen202.Adapter.AdaptadorRecepAlm;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.SetterandGetters.UbicacionesAjuste;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class ActivityAjusteUbi extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private int posicion=0,posicion2=0,posG=-1,TOTP=0,RECEP=0;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje,Producto="",serv,Folio="";
    private EditText txtCod,txtCantidad,txtMaxi,txtComentario,txtUbicac;
    private Button btnBuscar,btnAggUbi,btnGrd,btnTerm;
    private RecyclerView rvUbicaciones;
    private AdaptadorAjusteUbi adapter;
    ArrayList<UbicacionesAjuste> lista = new ArrayList<>();
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg;
    private int sonido_correcto,sonido_error;
    private SoundPool bepp;
    AlertDialog dialog6 = null;
    AlertDialog.Builder builder6;
    private AlertDialog alertDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuste_ubi);

        MyToolbar.show(this, "Ajuste Ubicación", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        extImg=preference.getString("ext", "null");

        switch (strServer) {
            case "sprautomotive.servehttp.com:9090":
                serv="RODATECH";
                break;
            case "sprautomotive.servehttp.com:9095":
                serv="PARTECH";
                break;
            case "sprautomotive.servehttp.com:9080":
                serv="TG";
                break;
        }

        mDialog = new SpotsDialog(ActivityAjusteUbi.this);

        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityAjusteUbi.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtCod    = findViewById(R.id.txtCod);
        btnBuscar = findViewById(R.id.btnBuscar);
        rvUbicaciones = findViewById(R.id.rvUbicaciones);
        txtCantidad      = findViewById(R.id.txtCantidad);
        txtMaxi  = findViewById(R.id.txtMaxi);
        btnAggUbi    = findViewById(R.id.btnAggUbi);
        txtComentario =findViewById(R.id.txtComentario);
        txtUbicac = findViewById(R.id.txtUbicac);
        btnGrd      = findViewById(R.id.btnGrd);
        btnTerm = findViewById(R.id.btnT);

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityAjusteUbi.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityAjusteUbi.this, R.raw.error, 1);

        rvUbicaciones    = findViewById(R.id.rvUbicaciones);
        rvUbicaciones.setLayoutManager(new LinearLayoutManager(ActivityAjusteUbi.this));
        adapter = new AdaptadorAjusteUbi(lista);
        keyboard = (InputMethodManager) getSystemService(ActivityRecepTraspMultSuc.INPUT_METHOD_SERVICE);

        //txtCod.setInputType(InputType.TYPE_NULL);
        txtCod.requestFocus();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtCod.getText().toString().equals("")){
                    posicion=0;
                    new AsyncListaUb(txtCod.getText().toString()).execute();
                }else{
                    Toast.makeText(ActivityAjusteUbi.this, "CAMPO VACIO", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnBuscar

        btnGrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cant=txtCantidad.getText().toString();
                String maxi=txtMaxi.getText().toString();
                String comen=txtComentario.getText().toString();
                cant=cant.trim();maxi=maxi.trim();comen=comen.trim();
                if(cant.equals("") || maxi.equals("")){
                    Toast.makeText(ActivityAjusteUbi.this, "Campos Vacios", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                    String finalCant = cant;
                    String finalMaxi = maxi;
                    String finalComen = comen;
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsyncActualiza(Producto,txtUbicac.getText().toString(),
                                    finalCant, finalMaxi, finalComen).execute();
                        }
                    });
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("¿DESEA GUARDAR DATOS?").create().show();

                }//else
            }
        });//btngrd

        btnAggUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cant=txtCantidad.getText().toString();
                String maxi=txtMaxi.getText().toString();
                String comen=txtComentario.getText().toString();
                cant=cant.trim();maxi=maxi.trim();comen=comen.trim();
                if(cant.equals("") || maxi.equals("")){
                    Toast.makeText(ActivityAjusteUbi.this, "Campos Vacios", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                    String finalCant = cant;
                    String finalMaxi = maxi;
                    String finalComen = comen;
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsyncAgg(Producto,txtUbicac.getText().toString(),
                                    finalCant, finalMaxi, finalComen).execute();
                        }
                    });
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("¿DESEA AGREGAR UBICACIÓN NO-UBICADO?").create().show();

                }//else

            }//onclcik
        });//

        btnTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cant=txtCantidad.getText().toString();
                String maxi=txtMaxi.getText().toString();
                String comen=txtComentario.getText().toString();
                cant=cant.trim();maxi=maxi.trim();comen=comen.trim();
                if(cant.equals("") || maxi.equals("")){
                    Toast.makeText(ActivityAjusteUbi.this, "Campos Vacios", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                    String finalCant = cant;
                    String finalMaxi = maxi;
                    String finalComen = comen;
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsynTermina(Producto,txtUbicac.getText().toString(),
                                    finalCant, finalMaxi, finalComen).execute();
                        }
                    });
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("¿DESEA TERMINAR CON ESTE CÓDIGO").create().show();
                }//else
            }//onclcik
        });//

    }//onCreate

    public boolean firtMet() {//firtMet
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {//si hay conexion a internet
            return true;
        }else {
            return false;
        }//else
    }//FirtMet saber si hay conexion a internet

    public void onClickLista(View v){
        keyboard.hideSoftInputFromWindow(txtCod.getWindowToken(), 0);
        keyboard.hideSoftInputFromWindow(txtCantidad.getWindowToken(), 0);
        keyboard.hideSoftInputFromWindow(txtMaxi.getWindowToken(), 0);
        keyboard.hideSoftInputFromWindow(txtComentario.getWindowToken(), 0);
        posicion = rvUbicaciones.getChildPosition(rvUbicaciones.findContainingItemView(v));
        txtCod.requestFocus();
        mostrarDetalle();
    }//onClickLista

    public void mostrarDetalle(){//detalle por ubi seleccionado
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvUbicaciones.scrollToPosition(posicion);

        txtUbicac.setText(lista.get(posicion).getUbicacione());
        txtCantidad.setText(lista.get(posicion).getCantidad());
        txtMaxi.setText(lista.get(posicion).getMaximo());
        txtComentario.setText(lista.get(posicion).getObservaciones());//
        posG=posicion;
    }//mostrarDetalleProd

    private class AsyncListaUb extends AsyncTask<Void, Void, Void> {

        private boolean conn;
        private String producto;
        boolean agg=true;
        public AsyncListaUb(String producto) {
            this.producto = producto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){
                mDialog.show();
            }
            Producto="";
            lista.clear();
            rvUbicaciones.setAdapter(null);
            txtCantidad.setEnabled(false);
            txtMaxi.setEnabled(false);
            txtComentario.setEnabled(false);
            txtUbicac.setText("");
            txtCantidad.setText("");
            txtMaxi.setText("");
            txtComentario.setText("");
            btnAggUbi.setEnabled(false);
            btnGrd.setEnabled(false);
            btnTerm.setEnabled(false);
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="sucursal="+strbran+"&producto="+producto;
                String url = "http://"+strServer+"/ListaUbicacionesPro?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        lista.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            lista.add(new UbicacionesAjuste(dato.getString("ubicaciones"),
                                    dato.getString("existencia"),dato.getString("maximo"),
                                    dato.getString("observaciones")));
                            if(dato.getString("ubicaciones").equals("NO-UBICADO")){
                                agg=false;

                            }
                            mensaje="";
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Sin ubicaciones";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if(lista.size()>0) {
                Producto=producto;
                mDialog.dismiss();
                txtCantidad.setEnabled(true);
                txtMaxi.setEnabled(true);
                txtComentario.setEnabled(true);
                adapter = new AdaptadorAjusteUbi(lista);
                rvUbicaciones.setAdapter(adapter);
                keyboard.hideSoftInputFromWindow(txtCod.getWindowToken(), 0);
                btnGrd.setEnabled(true);
                //btnAggUbi.setEnabled(true);
                btnTerm.setEnabled(true);
                btnAggUbi.setEnabled(agg);

                mostrarDetalle();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage("PUEDE QUE ESTE CÓDIGO YA ESTE PROCESADO O NO EXISTA").create().show();
            }//else
        }//onPost
    }//AsyncListaUb

    private class AsyncActualiza extends AsyncTask<Void, Void, Void> {
        boolean conn;
        private String prod="",ubicacion="",cantidad="",maximo="",comentario="";

        public AsyncActualiza(String prod, String ubicacion, String cantidad, String maximo, String comentario) {
            this.prod = prod;
            this.ubicacion = ubicacion;
            this.cantidad = cantidad;
            this.maximo = maximo;
            this.comentario = comentario;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecutive
        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                if(maximo.equals("")){maximo="0";}
                if(comentario.equals("")){comentario="";}
                String parametros="producto="+prod+"&ubicacion="+ubicacion+
                        "&cantidad="+cantidad+"&max="+maximo+
                        "&observaciones="+comentario+"&sucursal="+strbran+"&inserUbica=1";
                String url = "http://"+strServer+"/CambiarRCMOU?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("messege");
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al registrar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mensaje="Problema en el servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInbackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(mensaje.equals("Actualizado con exito") || mensaje.equals("Registro realizado con exito")){
                new AsyncListaUb(prod).execute();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPost
    }//AsyncActualiza

    private class AsyncAgg extends AsyncTask<Void, Void, Void> {
        boolean conn;
        private String prod,ubicacion,cantidad,maximo,comentario;

        public AsyncAgg(String prod, String ubicacion, String cantidad, String maximo, String comentario) {
            this.prod = prod;
            this.ubicacion = ubicacion;
            this.cantidad = cantidad;
            this.maximo = maximo;
            this.comentario = comentario;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecutive
        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                String parametros="producto="+prod+"&ubicacion="+ubicacion+
                        "&cantidad="+cantidad+"&max="+maximo+
                        "&observaciones="+comentario+"&sucursal="+strbran+"&inserUbica=0";
                String url = "http://"+strServer+"/CambiarRCMOU?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("messege");
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al registrar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mensaje="Problema en el servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInbackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(mensaje.equals("Actualizado con exito") || mensaje.equals("Registro realizado con exito")){
                new AsyncListaUb(prod).execute();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPost
    }//AsyncAsyncAgg

    private class AsynTermina extends AsyncTask<Void, Void, Void> {
        boolean conn;
        private String prod,ubicacion,cantidad,maximo,comentario;

        public AsynTermina(String prod, String ubicacion, String cantidad, String maximo, String comentario) {
            this.prod = prod;
            this.ubicacion = ubicacion;
            this.cantidad = cantidad;
            this.maximo = maximo;
            this.comentario = comentario;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecutive
        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                String parametros="producto="+prod+"&ubicacion="+ubicacion+
                        "&cantidad="+cantidad+"&max="+maximo+
                        "&observaciones="+comentario+" &sucursal="+strbran+"&inserUbica=1&Terminado=TERMINADO";
                String url = "http://"+strServer+"/CambiarRCMOU?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("messege");
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al registrar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mensaje="Problema en el servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInbackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(mensaje.equals("Terminaste")){
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncListaUb(prod).execute();
                    }
                });
                AlertDialog dialog = builder.create();

            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAjusteUbi.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPost
    }//AsyncTermina

}//Activity