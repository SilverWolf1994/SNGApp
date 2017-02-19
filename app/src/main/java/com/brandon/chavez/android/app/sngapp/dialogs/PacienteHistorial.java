package com.brandon.chavez.android.app.sngapp.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.AdapterPacienteHistoriales;
import com.brandon.chavez.android.app.sngapp.utilidades.AdapterPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosPacienteHistoriales;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PacienteHistorial extends AppCompatActivity implements AdapterPacienteHistoriales.OnHistorialSelected {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private TextView tvPacienteHistorialF;
    private RecyclerView rvPacienteHistorialesF;

    private DatosPacienteHistoriales datosPacienteHistoriales;
    private List<DatosPacienteHistoriales> datosHistorialesListados = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private AdapterPacienteHistoriales adapterPacienteHistoriales;

    private String codigoUsuario, codigoPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_historial);
        thisActivity = this;
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        Intent intentpacienteslistado = getIntent();
        codigoPaciente = intentpacienteslistado.getStringExtra("CODIGO_PACIENTE");

        tvPacienteHistorialF = (TextView) findViewById(R.id.tvPacienteHistorial);
        rvPacienteHistorialesF = (RecyclerView) findViewById(R.id.rvPacienteHistoriales);

        listadoHistorialesBD(codigoUsuario, codigoPaciente);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentpacientehistorialesregistrar = new Intent(thisActivity, PacienteHistorialesRegistrar.class);
                intentpacientehistorialesregistrar.putExtra("CODIGO_USUARIO", codigoUsuario);
                intentpacientehistorialesregistrar.putExtra("CODIGO_PACIENTE", codigoPaciente);
                startActivity(intentpacientehistorialesregistrar);
            }
        });
    }

    @Override
    public void onHistorialClick(DatosPacienteHistoriales datosPacienteHistoriales)
    {
        //Toast.makeText(thisActivity, datosPacientesListado.getCodigoPaciente(), Toast.LENGTH_LONG).show();
    }

    public void listadoHistorialesBD(String codigo_usuario, String codigo_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 5);
        params.put("codigo_usuario", codigo_usuario);
        params.put("codigo_paciente", codigo_paciente);

        try{

            httpclient.post(servidor.urlServidorControlHistorial, params, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    progressDialog = new ProgressDialog(thisActivity);
                    progressDialog.setMessage("Procesando \nPor favor espere...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        progressDialog.dismiss();

                        if (response.getJSONArray("Historiales").length() > 0) {
                            for (int i = 0; i < response.getJSONArray("Historiales").length(); i++)
                            {
                                datosPacienteHistoriales = new DatosPacienteHistoriales();
                                datosPacienteHistoriales.setCodigoHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Codigo_Historial"));
                                datosPacienteHistoriales.setNombreHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Nombre_Historial"));
                                datosPacienteHistoriales.setDescripcionHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Descripcion_Historial"));
                                datosPacienteHistoriales.setAlturaHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Altura_Historial"));
                                datosPacienteHistoriales.setPesoHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Peso_Historial"));
                                datosPacienteHistoriales.setImcHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Imc_Historial"));
                                datosPacienteHistoriales.setFechaHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Fecha_Historial"));
                                datosPacienteHistoriales.setHoraHistorial(response.getJSONArray("Historiales").getJSONObject(i).getString("Hora_Historial"));
                                datosHistorialesListados.add(datosPacienteHistoriales);
                            }
                            adapterPacienteHistoriales = new AdapterPacienteHistoriales(thisActivity, datosHistorialesListados, PacienteHistorial.this);
                            staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                            rvPacienteHistorialesF.setLayoutManager(staggeredGridLayoutManager);
                            rvPacienteHistorialesF.setHasFixedSize(true);
                            rvPacienteHistorialesF.setAdapter(adapterPacienteHistoriales);

                        } else {
                            tvPacienteHistorialF.setVisibility(View.VISIBLE);
                            rvPacienteHistorialesF.setVisibility(View.GONE);
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    //Snackbar.make(clUsuarioLoginA, "Verifique la conexión con el servidor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            });

        }catch (Exception exception) {
            Toast.makeText(thisActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

}
