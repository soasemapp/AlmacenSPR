package com.almacen.alamacen202.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AduanaAdapter;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Aduana;
import com.almacen.alamacen202.includes.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AduanaActivity extends AppCompatActivity {
    private static final String TAG = "AduanaActivity";
    private RecyclerView recyclerView;
    private AduanaAdapter adapter;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    String strusr, strpass, StrServer, strcodBra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aduana);

        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();

        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        StrServer = preference.getString("Server", "null");
        strcodBra = preference.getString("codBra", "null");

        recyclerView = findViewById(R.id.recyclerViewAduana);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        new ObtenerAduanasTask().execute(StrServer, strcodBra);
    }

    private class ObtenerAduanasTask extends AsyncTask<String, Void, List<Aduana>> {

        @Override
        protected List<Aduana> doInBackground(String... params) {
            String server = params[0];
            String sucursal = params[1];
            String urlString = "http://" + server + "/ListaduanaAlm?sucursal=" + sucursal;
            Log.d(TAG, "URL de la petición: " + urlString);

            List<Aduana> listaAduanas = new ArrayList<>();

            HttpHandler httpHandler = new HttpHandler();
            String jsonResponse = httpHandler.makeServiceCall(urlString, strusr, strpass);
            Log.d(TAG, "Respuesta del servidor: " + jsonResponse);

            try {
                if (jsonResponse != null) {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("Almacen");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject aduanaObject = jsonArray.getJSONObject(i);
                        Aduana aduana = new Aduana();
                        aduana.setCliente(aduanaObject.getString("cliente"));
                        aduana.setNombre(aduanaObject.getString("nombre"));
                        aduana.setFolio(aduanaObject.getString("folio"));
                        aduana.setFecha(aduanaObject.getString("fecha"));
                        aduana.setReferencia(aduanaObject.getString("referencia"));
                        aduana.setDocumento(aduanaObject.getString("documento"));
                        aduana.setCantidad(aduanaObject.getInt("cantidad"));
                        aduana.setCantidadSurt(aduanaObject.getInt("cantidadsurt"));
                        aduana.setUrgencia(aduanaObject.getString("urgencia"));
                        listaAduanas.add(aduana);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error al procesar las aduanas: ", e);
            }

            return listaAduanas;
        }

        @Override
        protected void onPostExecute(List<Aduana> listaAduanas) {
            if (listaAduanas != null && !listaAduanas.isEmpty()) {
                adapter = new AduanaAdapter(AduanaActivity.this, listaAduanas,strcodBra);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(AduanaActivity.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
