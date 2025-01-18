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
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorRecepAlm;
import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
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

public class ActivityRecepAlm extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private boolean datos=false,modificados=false;
    private int posicion=0,posG=-1,TOTP=0,RECEP=0;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje,Producto="",serv,Folio="",impresora;
    private ArrayList<Traspasos> listaTrasp = new ArrayList<>();
    private EditText txtProd,txtCantidad,txtCantSurt,txtUbicT,txtCantidadS;
    private AutoCompleteTextView spAlm;
    private ImageView ivProd;
    private TextView tvProd;
    private Button btnBuscar,btnAtras,btnAdelante,btnCorr,btnBusc,btnGuarda;
    private CheckBox chManual;
    private RecyclerView rvTraspasos;
    private AdaptadorRecepAlm adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg,tipoRecp="";
    //private int sonido_correcto,sonido_error;
    //private SoundPool bepp;
    private MediaPlayer mpError,mpCorrecto;
    private Intent dwIntent;
    Context context = this;
    AlertDialog dialog6 = null;
    AlertDialog.Builder builder6;
    private ArrayList<String> listaNomAlm = new ArrayList<>();
    private AlertDialog alertDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recep_alm);

        MyToolbar.show(this, "Recepción Almacén Morelos", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        impresora = preference.getString("Impresora", "null");
        urlImagenes=preference.getString("urlImagenes", "null");
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

        mDialog = new SpotsDialog(ActivityRecepAlm.this);

        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityRecepAlm.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtProd    = findViewById(R.id.txtProducto);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtCantSurt = findViewById(R.id.txtCantSurt);
        txtCantidadS = findViewById(R.id.txtCantidadS);
        tvProd      = findViewById(R.id.tvProd);
        btnBuscar  = findViewById(R.id.btnBuscar);
        btnAtras    = findViewById(R.id.btnAtras);
        btnAdelante =findViewById(R.id.btnAdelante);
        ivProd      = findViewById(R.id.ivProd);
        btnCorr     = findViewById(R.id.btnCorr);
        //txtTotPza = findViewById(R.id.txtTotPza);
        txtUbicT = findViewById(R.id.txtUbicT);
        //btnImpr = findViewById(R.id.btnImpr);
        spAlm = findViewById(R.id.spAlm);
        btnBusc = findViewById(R.id.btnBusc);
        btnGuarda = findViewById(R.id.btnGuarda);
        chManual = findViewById(R.id.chManual);

        /*bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityRecepAlm.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityRecepAlm.this, R.raw.error, 1);*/
        mpCorrecto = MediaPlayer.create(context, R.raw.sonido_correct);
        mpError=MediaPlayer.create(context, R.raw.error);

        rvTraspasos    = findViewById(R.id.rvTraspasos);
        rvTraspasos.setLayoutManager(new LinearLayoutManager(ActivityRecepAlm.this));
        adapter = new AdaptadorRecepAlm(listaTrasp);
        keyboard = (InputMethodManager) getSystemService(ActivityRecepTraspMultSuc.INPUT_METHOD_SERVICE);

        txtProd.setInputType(InputType.TYPE_NULL);
        //txtProd.requestFocus();

        listaNomAlm.add("---Seleccione tipo de recepción---");
        listaNomAlm.add("Recepción Guadalajara");listaNomAlm.add("Recepción Fresnillo");
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                ActivityRecepAlm.this,R.layout.drop_down_item,listaNomAlm);
        spAlm.setAdapter(adaptador);
        spAlm.setText(listaNomAlm.get(0),false);

        spAlm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                limpiar();
                rvTraspasos.setAdapter(null);
                if(position==1){//recepcion guadalajara
                    tipoRecp="G";
                }else if(position==2){
                    tipoRecp="F";
                }else{
                    tipoRecp="";
                }
            }//onItemClick
        });

        chManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listaTrasp.size()>0){
                    listaTrasp.get(posG).setCantSurt("0");
                    listaTrasp.get(posG).setSincronizado(true);
                    mostrarDetalleProd();
                }//if
            }
        });//setonche

        btnGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.hideSoftInputFromWindow(txtCantSurt.getWindowToken(), 0);
                if(!txtCantSurt.getText().toString().equals("") &&
                        Integer.parseInt(txtCantSurt.getText().toString())>0){
                    int cantsinc=Integer.parseInt(listaTrasp.get(posicion).getCantSinc());
                    int cantsurt=Integer.parseInt(txtCantSurt.getText().toString());
                    int cant=Integer.parseInt(listaTrasp.get(posicion).getCantidad());
                    if((cantsinc+cantsurt)<=cant){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncActualizar(listaTrasp.get(posicion).getProducto(),
                                        cantsurt+"","change",false,Producto,tipoRecp).execute();
                            }
                        });
                        builder.setNegativeButton("CANCELAR",null);
                        builder.setCancelable(false);
                        builder.setTitle("AVISO");
                        builder.setMessage("¿DESEA SINCRONIZAR "+cantsurt+" PIEZAS DE "+
                                listaTrasp.get(posicion).getProducto()+"?").create().show();

                    }else{
                        mpError.start();
                        Toast.makeText(ActivityRecepAlm.this, "Sobrepasa Cantidad", Toast.LENGTH_SHORT).show();
                    }//else
                }else{
                    Toast.makeText(ActivityRecepAlm.this, "Cantidad en 0", Toast.LENGTH_SHORT).show();
                }//else
            }
        });


        btnBusc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posG=posicion;
                    new AsyncActualizar(listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "alertbusca",false,Producto,tipoRecp).execute();

                }else{
                    if(listaTrasp.size()>0) {
                        alertBusca();
                    }else{
                        Toast.makeText(context, "Sin datos para buscar", Toast.LENGTH_SHORT).show();
                    }//else
                }//else
            }//onclcik
        });

        txtProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Producto=editable.toString();
                if(!editable.toString().equals("")){
                    if (codeBar.equals("Zebra")) {
                        Producto=Producto.trim();
                        Producto=Producto.replaceAll("(\n|\r)", "");
                        accionEscanea(Producto);
                        if(chManual.isChecked() ){
                            txtCantSurt.requestFocus();
                            txtProd.setText("");
                        }else{
                            txtProd.setText("");
                            txtProd.requestFocus();
                        }
                    }else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                accionEscanea(Producto);
                                if(chManual.isChecked() ){
                                    txtCantSurt.requestFocus();
                                    txtProd.setText("");
                                }else{
                                    txtProd.setText("");
                                    txtProd.requestFocus();
                                }
                                break;
                            }//if
                        }//for
                    }//else
                }//if es diferente a vacio
            }//after
        });//txtProd textchange

        txtProd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId ==0) {
                    txtProd.requestFocus();
                    return true;
                }//if action done
                return false;
            }//oneditoraction
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posG=posicion;
                    new AsyncActualizar(listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto,tipoRecp).execute();
                }else {
                    if(tipoRecp.equals("")){
                        Toast.makeText(ActivityRecepAlm.this, "Selecciona Tipo de Recepción", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rvTraspasos.setAdapter(null);
                    limpiar();
                    posicion=0;
                    new AsyncReceConSinFol().execute();
                }//else
            }//onclick
        });//btnGuardar setonclick

        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion=posG;
                cambio("next",false);
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion=posG;
                cambio("back",false);
            }//onclick
        });//btnatras setonclicklistener

        btnCorr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.get(posicion).isSincronizado()==false){
                    posG=posicion;
                    new AsyncActualizar(listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto,tipoRecp).execute();
                }else {
                    Toast.makeText(ActivityRecepAlm.this, "Sin cambios", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnCorr



        dwIntent = new Intent();

        //new AsyncConsulAlm().execute();
    }//onCreate

    @Override
    protected void onStop() {
        super.onStop();
        mpCorrecto.stop();
        mpError.stop();
        mpCorrecto = MediaPlayer.create(context, R.raw.sonido_correct);
        mpError=MediaPlayer.create(context, R.raw.error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mpCorrecto.stop();
        mpError.stop();
    }


    public String folio(String folio){
        if (folio.length() < 7) {
            int fo = folio.length();
            switch (fo) {
                case 1:
                    folio = "000000" + folio;
                    break;
                case 2:
                    folio = "00000" + folio;
                    break;
                case 3:
                    folio ="0000" + folio;
                    break;
                case 4:
                    folio ="000" + folio;
                    break;
                case 5:
                    folio ="00" + folio;
                    break;
                case 6:
                    folio = "0" + folio;
                    break;
                default:
                    folio=folio;
                    break;
            }//switch
        }//if
        return folio;
    }

    public void accionEscanea(String prod){
        String tt=tvProd.getText().toString();
        if(chManual.isChecked()){
            buscar(prod,false);
        } else if(tt.equals(prod)){//SIGNIFICA QUE ES EL MISMO CODIGO QUE ESTA SELECCIONADO
            actualizaDat(posicion,prod);
        }else{//
            mpError.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setTitle("AVISO");
            builder.setMessage("Escaneo de un código diferente");
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }//else
    }//accionEscanea


    public void buscar(String comparar,boolean sumar){
        boolean existe=false;
        for(int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).getProducto().equals(comparar)){
                existe=true;
                if(alertDialog!=null && alertDialog.isShowing()){
                    alertDialog.dismiss();
                    btnBusc.setEnabled(true);
                }
                if(sumar==true){
                    actualizaDat(i,comparar);
                }else{
                    posicion=i;
                    mostrarDetalleProd();
                }//else
                break;
            }//if
        }
        if(existe==false){
            btnBusc.setEnabled(true);
            mpError.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setTitle("AVISO");
            builder.setMessage("No existe "+Producto+" en la lista");
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialogg = builder.create();
            dialogg.show();
        }
    }//evaluar

    public void actualizaDat(int pos,String prod){
        dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "DISABLE_PLUGIN");
        sendBroadcast(dwIntent);
        posicion=pos;
        int cant=Integer.parseInt(listaTrasp.get(pos).getCantidad());
        int recep=Integer.parseInt(listaTrasp.get(pos).getCantSinc());//cantidad de ya escaneados
        int cantS=Integer.parseInt(listaTrasp.get(pos).getCantSurt());
        if(recep+(cantS+1)<=cant){
            cantS++;
            listaTrasp.get(pos).setCantSurt(cantS+"");
            listaTrasp.get(pos).setSincronizado(false);
            RECEP++;
            modificados=true;
            if((recep+cantS)==cant){
                posG=pos;
                new AsyncActualizar(prod,cantS+"","change",false,Producto,tipoRecp).execute();
            }else{
                dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
                sendBroadcast(dwIntent);//HABILITAR ESCANER
                listaTrasp.get(pos).setCantSurt(cantS+"");
                listaTrasp.get(pos).setSincronizado(false);
                mostrarDetalleProd();
            }
        }else if((recep+cantS)>cant){
            mpError.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
                    sendBroadcast(dwIntent);//HABILITAR ESCANER
                    listaTrasp.get(posG).setCantSurt("0");
                    mostrarDetalleProd();
                }
            });
            builder.setCancelable(false);
            builder.setNegativeButton("CANCELAR",null);
            builder.setMessage("LAS PIEZAS ESCANEADAS EXCEDEN EL LIMITE\n" +
                    "¿DESEA REINICIAR EL CONTEO DE LAS PIEZAS ESCANEADAS QUE ESTÁN SIN SINCRONIZAR?\n)");
            builder.setTitle("AVISO").create().show();
        }else{
            mpError.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
                    sendBroadcast(dwIntent);//HABILITAR ESCANER
                }
            });
            builder.setCancelable(false);
            builder.setTitle("Excede cantidad").create().show();
        }
    }//actualiza por codigo



    public void alertBusca(){
        btnBusc.setEnabled(false);
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityRecepAlm.this);
        LayoutInflater inflater = ActivityRecepAlm.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_buscprod, null);
        alert.setView(dialogView);
        TextView tvTit = dialogView.findViewById(R.id.tvTit);
        EditText txtBuscaP = dialogView.findViewById(R.id.txtBuscaP);
        Button btnB = dialogView.findViewById(R.id.btnB);
        tvTit.setText("Buscar Producto");

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtBuscaP.getText().toString().equals("")){
                    String comparar=txtBuscaP.getText().toString().trim();
                    buscar(comparar,false);
                }else{
                    Toast.makeText(ActivityRecepAlm.this, "Campo Vacío", Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnB

        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                keyboard.hideSoftInputFromWindow(txtBuscaP.getWindowToken(), 0);
                btnBusc.setEnabled(true);
                txtProd.requestFocus();
            }
        });//cerrar

        alertDialog = alert.create();
        alertDialog.show();
        txtBuscaP.requestFocus();
    }//alertBusca

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

    public void cambiaProd(){
        if(posicion==0 && listaTrasp.size()>1){
            btnAdelante.setEnabled(true);
            btnAtras.setEnabled(false);
        }else if(posicion+1==listaTrasp.size() && listaTrasp.size()>1){
            btnAtras.setEnabled(true);
            btnAdelante.setEnabled(false);
        }else if(listaTrasp.size()==1){
            btnAtras.setEnabled(false);
            btnAdelante.setEnabled(false);
        }else{
            btnAtras.setEnabled(true);
            btnAdelante.setEnabled(true);
        }//else
    }//cambiaProd

    public void onClickLista(View v){//cada vez que se seleccione un producto en la lista
        posicion= rvTraspasos.getChildPosition(rvTraspasos.findContainingItemView(v));
        cambio("change",false);
    }//onClickLista




    public void cambio(String var,boolean sumar){
        if(listaTrasp.get(posG).isSincronizado() == false){//identificando que prod anterior no se sincronizó
            new AsyncActualizar(listaTrasp.get(posG).getProducto(),
                    listaTrasp.get(posG).getCantSurt(),var,sumar,Producto,tipoRecp).execute();
        }else{//cuando se escanea o por botones de adelante, atras y onclick en lista
            posG=posicion;
            tipoCambio(var);
            if(chManual.isChecked() && !Producto.equals("")){
                buscar(Producto,false);
            }else if(sumar==true){//al escanear
                actualizaDat(posicion,Producto);
            }else{
                mostrarDetalleProd();
            }
        }//else
    }//alert

    public void tipoCambio(String var){
        switch (var){
            case "next":
                posicion++;break;
            case "back":
                posicion--;break;
            case "change":
                posicion=posG;
                posG=0;break;
            default:posicion=encontrarPosEnLista(var);break;
        }
    }
    public int totPazas(){
        int tot=0;
        for (int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).isSincronizado()==true){
                tot=tot+Integer.parseInt(listaTrasp.get(i).getCantSurt());
            }
        }
        return tot;
    }

    public void mostrarDetalleProd(){//detalle por producto seleccionado
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        //rvTraspasos.getAdapter().notifyItemChanged(posicion);
        rvTraspasos.scrollToPosition(posicion);
        //Producto=listaTrasp.get(posicion).getProducto();
        tvProd.setText(listaTrasp.get(posicion).getProducto());
        txtCantidad.setText(listaTrasp.get(posicion).getCantidad());
        txtCantidadS.setText(listaTrasp.get(posicion).getCantSinc());

        txtCantSurt.setText(listaTrasp.get(posicion).getCantSurt());
        btnCorr.setEnabled(false);

        int totsurt=Integer.parseInt(listaTrasp.get(posicion).getCantSurt())+
                Integer.parseInt(listaTrasp.get(posicion).getCantSinc());

        if(Integer.parseInt(txtCantidad.getText().toString())==totsurt){//cuando ya se completo la cantidad
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorVerdeAzul)));
            if(chManual.isChecked() && listaTrasp.get(posicion).isSincronizado()){
                txtCantSurt.setEnabled(false);
                btnGuarda.setEnabled(false);
            }//if
            txtProd.requestFocus();
        }else{
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            if(!listaTrasp.get(posicion).isSincronizado() && totsurt>0){//para que se habilite boton de sincronizar
                txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAzz)));
                btnCorr.setEnabled(true);
            }
            if(chManual.isChecked() ){
                txtCantSurt.setEnabled(true);
                btnGuarda.setEnabled(true);
                btnGuarda.setEnabled(true);
                txtCantSurt.setText("");
                txtCantSurt.requestFocus();
            }else{
                txtCantSurt.setEnabled(false);
                btnGuarda.setEnabled(false);
                txtProd.requestFocus();
            }
        }//else

        cambiaProd();
        posG=posicion;
    }//mostrarDetalleProd

    public void limpiar(){
        tvProd.setText("");
        txtCantidad.setText("");
        txtCantSurt.setText("");
        txtCantidadS.setText("");
        ivProd.setImageResource(R.drawable.logokepler);
        txtUbicT.setText("");
        btnAtras.setEnabled(false);
        btnAdelante.setEnabled(false);
        btnCorr.setEnabled(false);
        posG=-1;
    }//limpiar

    public int encontrarPosEnLista(String prod){
        int p=posG;
        for(int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).getProducto().equals(prod)){
                p=i;
                break;
            }//if
        }
        return p;
    }

    public boolean surtTodos(){
        boolean surt=false;
        int c=0;
        for(int i=0;i<listaTrasp.size();i++){
            int cant=Integer.parseInt(listaTrasp.get(i).getCantidad());
            int recep=Integer.parseInt(listaTrasp.get(i).getCantSinc());//campo usado para guardar lo que ya fue surtido
            int cantS=Integer.parseInt(listaTrasp.get(i).getCantSurt());
            if(cant==(cantS+recep)){
                c++;
            }
        }
        if(c==listaTrasp.size()){
            surt=true;
        }
        return surt;
    }


    public void verLista(){
        txtProd.requestFocus();
        adapter = new AdaptadorRecepAlm(listaTrasp);
        rvTraspasos.setAdapter(adapter);
        txtProd.setEnabled(true);
        txtProd.requestFocus();
        btnBusc.setEnabled(true);
        if(posicion>=listaTrasp.size()){
            posicion=listaTrasp.size()-1;
            btnBusc.setEnabled(false);
        }
        mostrarDetalleProd();
    }//ver lista

    private class AsyncReceConSinFol extends AsyncTask<Void, Void, Void> {

        private boolean conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){
                mDialog.show();
            }
            chManual.setEnabled(false);
            rvTraspasos.setAdapter(null);
            limpiar();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="sucursal="+strbran+"&k_alm=60";
                String url = "http://"+strServer+"/RecepMultSucSinFol?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num=1;
                        listaTrasp.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaTrasp.add(new Traspasos(num+"",dato.getString("PRODUCTO")
                                    ,dato.getString("CANTIDAD"),dato.getString("UBICACION"),
                                    dato.getString("RECEPCION"),"0","0",true));
                            num++;
                            mensaje="";
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Puede que no exista este almacén o no tenga códigos con existencia";
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
            if(mensaje.equals("")) {
                mDialog.dismiss();
                chManual.setEnabled(true);
                verLista();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncReceConSinFol

    private class AsyncActualizar extends AsyncTask<Void, Void, Void> {

        private String producto,cantidad,var,ProductoActual,newCant="",sob="",tiporec;
        private boolean conn=true,sumar;
        public AsyncActualizar(String producto, String cantidad,
                               String var,boolean sumar,String ProductoActual,String tiporec) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.var=var;
            this.sumar=sumar;
            this.ProductoActual=ProductoActual;
            this.tiporec=tiporec;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                String parametros="k_Sucursal="+strbran+"&k_Producto="+producto+
                        "&k_Cantidad="+cantidad+"&k_tiporec="+tiporec+"";
                String url = "http://"+strServer+"/InsertAlm?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("MENSAJE");
                        newCant=dato.getString("CANT");
                        sob=dato.getString("SOB");
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Sin sincronizar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problemas con el servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if(conn==false){
                mDialog.dismiss();
                Toast.makeText(ActivityRecepAlm.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }else if (mensaje.equals("SINCRONIZADO")) {
                mDialog.dismiss();
                mpCorrecto.start();
                dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
                sendBroadcast(dwIntent);//HABILITAR ESCANER
                listaTrasp.get(posG).setCantSinc(newCant);
                listaTrasp.get(posG).setCantSurt("0");
                listaTrasp.get(posG).setSincronizado(true);
                if(var.equals("alertbusca")){//
                    adapter.notifyDataSetChanged();
                    alertBusca();
                    return;
                }
                tipoCambio(var);
                if(sumar==true){
                    actualizaDat(posicion,listaTrasp.get(posicion).getProducto());
                }else{
                    mostrarDetalleProd();
                }//else

            }else if(mensaje.equals("DIF")){
                mpError.start();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                        listaTrasp.get(posG).setCantSinc(newCant);
                        listaTrasp.get(posG).setCantSurt(cantidad);
                        listaTrasp.get(posG).setSincronizado(false);
                        mostrarDetalleProd();
                        if(chManual.isChecked()) {
                            txtCantSurt.setText(listaTrasp.get(posicion).getCantSurt());//
                        }

                    }
                });
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncActualizar(producto,sob,var,sumar,ProductoActual,tiporec).execute();
                    }
                });
                builder.setCancelable(false);
                builder.setTitle("AVISO");
                builder.setMessage("YA HAY "+newCant+" PIEZAS SOLO SE PUEDEN SINCRONIZAR "+
                        sob+" ¿DESEA SINCRONIZARLAS?\n\n"+
                        "(NO SE TOMARÁN EN CUENTA "+(Integer.parseInt(cantidad)-Integer.parseInt(sob))+" PIEZAS)").create().show();

            }else{
                mDialog.dismiss();
                if(newCant.equals("")){newCant=listaTrasp.get(posG).getCantSinc();}
                if(sob.equals("")){sob=listaTrasp.get(posG).getCantSurt();}
                mpError.start();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        posicion=0;
                        new AsyncReceConSinFol().execute();
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listaTrasp.get(posG).setCantSinc(newCant);
                        listaTrasp.get(posG).setCantSurt(sob);
                        listaTrasp.get(posG).setSincronizado(false);
                        mostrarDetalleProd();
                    }
                });
                builder.setCancelable(false);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje+
                        "\n\n¿DESEA ACTUALIZAR DATOS?(SI ACTUALIZA LAS PIEZAS QUE NO SE HAYAN ESCANEADO SE PERDERÁN)").create().show();

            }//else
        }//onPost
    }//AsyncActualizar

    @Override
    public void onBackPressed() {
        if(modificados==true){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("CANCELAR",null);
            builder.setCancelable(false);
            builder.setTitle("AVISO").setMessage("Se hicieron movimientos ¿desea salir?").create().show();
        }else{
            finish();
        }
    }//onBackPressed

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoverflow5, menu);
        MenuItem itemOtro = menu.findItem(R.id.itOtro);
        itemOtro.setTitle("Traspaso");
        return true;
    }//onCreateOptionsMenu

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itOtro:
                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posG=posicion;
                    new AsyncActualizar(listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto,tipoRecp).execute();
                }else {
                    startActivity(new Intent(ActivityRecepAlm.this, ActivityRecepTraspMultSuc.class));
                    finish();
                }//else
                break;
        }
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected



}//Activity