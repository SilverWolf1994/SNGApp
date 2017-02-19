package com.brandon.chavez.android.app.sngapp.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.UsuarioLogin;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.brandon.chavez.android.app.sngapp.utilidades.WekaDatos;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class PacienteHistorialesRegistrar extends AppCompatActivity {

    private Activity thisActivity;
    private WekaDatos wekaDatos;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private EditText etNombreHistorialA, etDescripcionHistorialA, etAlturaHistorialA, etPesoHistorialA;
    private Spinner spMesHistorialA;

    private String codigoUsuario, codigoPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_historiales_registrar);
        thisActivity = this;
        wekaDatos = new WekaDatos(thisActivity);
        servidor = new Servidor();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intentpacientehistorial = getIntent();
        codigoUsuario = intentpacientehistorial.getStringExtra("CODIGO_USUARIO");
        codigoPaciente = intentpacientehistorial.getStringExtra("CODIGO_PACIENTE");

        etNombreHistorialA = (EditText) findViewById(R.id.etNombreHistorial);
        etDescripcionHistorialA = (EditText) findViewById(R.id.etDescripcionHistorial);
        etAlturaHistorialA = (EditText) findViewById(R.id.etAlturaHistorial);
        etPesoHistorialA = (EditText) findViewById(R.id.etPesoHistorial);
        spMesHistorialA = (Spinner) findViewById(R.id.spMesHistorial);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabHistorialRegistrar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = etNombreHistorialA.getText().toString();
                String descripcion = etDescripcionHistorialA.getText().toString();
                Float altura = (float) Math.round(Float.parseFloat(etAlturaHistorialA.getText().toString()) * 100f) / 100f;
                Float peso = (float) Math.round(Float.parseFloat(etPesoHistorialA.getText().toString()) * 100f) / 100f;
                int mes = spMesHistorialA.getSelectedItemPosition() + 1;
                Float imc =  calcularImc(peso, altura);
                String caso = wekaDatos.wekaPredictionEstado(imc, 5);
                registrarHistorialBD(codigoUsuario, codigoPaciente, generarCodigoHistorial(), nombre, descripcion, altura, peso, mes, imc, caso, obtenerFecha(), obtenerHora());
            }
        });
    }

    public void registrarHistorialBD(String codigo_usuario, String codigo_paciente, String codigo_historial,
                                     String nombre_historial, String descripcion_historial,
                                     Float altura_historial, Float peso_historial, int mes_historial, Float imc_historial, String caso_historial,
                                     String fecha_historial, String hora_historial)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        try {
            params.put("opcion", 1);
            params.put("codigo_usuario", codigo_usuario);
            params.put("codigo_paciente", codigo_paciente);
            params.put("codigo_historial", codigo_historial);
            params.put("nombre_historial", nombre_historial);
            params.put("descripcion_historial", descripcion_historial);
            params.put("altura_historial", String.valueOf(altura_historial));
            params.put("peso_historial", String.valueOf(peso_historial));
            params.put("mes_historial", String.valueOf(mes_historial));
            params.put("imc_historial", String.valueOf(imc_historial));
            params.put("caso_historial", caso_historial);
            params.put("fecha_historial", fecha_historial);
            params.put("hora_historial", hora_historial);

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error en los datos", Toast.LENGTH_SHORT).show();
        }

        try {
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

                        int respuesta = response.getInt("Success");
                        if (respuesta == 1)
                        {
                            Intent intentpacientehistorial = new Intent(thisActivity, PacienteHistorial.class);
                            intentpacientehistorial.putExtra("CODIGO_PACIENTE", codigoPaciente);
                            Toast.makeText(thisActivity, "Registro exitoso!", Toast.LENGTH_LONG).show();
                            startActivity(intentpacientehistorial);
                            onFinish();

                        } else if (respuesta == 0)
                        {
                            //Snackbar.make(clUsuarioRegistrarA, response.getString("Mensaje_Error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }catch (JSONException jsonexception) {
                        progressDialog.dismiss();
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    //Snackbar.make(clUsuarioRegistrarA, "Verifique la conexión con el servidor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            });

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

    public Float calcularImc(Float peso, Float altura)
    {
        return Math.round(peso/(altura * altura) * 100f) / 100f;
    }

    public String generarCodigoHistorial()
    {
        Calendar calendar = Calendar.getInstance();
        String stringCodigoHistorial = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "" + String.valueOf(calendar.get(Calendar.MONTH)+1) + "" + String.valueOf(calendar.get(Calendar.YEAR)) + "" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + "" + String.valueOf(calendar.get(Calendar.MINUTE)) + ""+ String.valueOf(calendar.get(Calendar.SECOND)) + ""+ String.valueOf(calendar.get(Calendar.MILLISECOND));
        stringCodigoHistorial = "HC" + stringCodigoHistorial;
        return stringCodigoHistorial;
    }

    public String obtenerFecha()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public String obtenerHora()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        return simpleDateFormat.format(calendar.getTime());
    }

}
