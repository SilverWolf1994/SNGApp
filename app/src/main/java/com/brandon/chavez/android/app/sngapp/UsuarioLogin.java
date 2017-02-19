package com.brandon.chavez.android.app.sngapp;

import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class UsuarioLogin extends AppCompatActivity {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private CoordinatorLayout clUsuarioLoginA;
    private EditText etCodigoLoginA, etPasswordLoginA;
    private Spinner spTipoUsuarioLoginA;
    private Button bRegistrarUsuarioLoginA;
    private FloatingActionButton fabLoginA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_login);
        thisActivity = this;
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (datosSharedPreferences.isLoggedIn())
        {
            datosSharedPreferences.checkLogin();
            HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
            String tipoUsuario = datosUsuario.get(DatosSharedPreferences.TIPO_USUARIO);
            if (tipoUsuario.equals("NUTRICIONISTA")) {
                startActivity(new Intent(thisActivity, UsuarioPerfil.class));
            } else if (tipoUsuario.equals("MEDICO GENERAL")) {
                startActivity(new Intent(thisActivity, UsuarioMedico.class));
            }
        }

        clUsuarioLoginA = (CoordinatorLayout) findViewById(R.id.clUsuarioLogin);
        etCodigoLoginA = (EditText) findViewById(R.id.etCodigoUsuarioLogin);
        etPasswordLoginA = (EditText) findViewById(R.id.etPasswordUsuarioLogin);
        spTipoUsuarioLoginA = (Spinner) findViewById(R.id.spTipoUsuarioLogin);
        bRegistrarUsuarioLoginA = (Button) findViewById(R.id.bRegistrarUsuarioLogin);
        fabLoginA = (FloatingActionButton) findViewById(R.id.fabUsuarioLogin);

        bRegistrarUsuarioLoginA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentusuarioregistrar = new Intent(thisActivity, UsuarioRegistrar.class);
                startActivity(intentusuarioregistrar);
            }
        });

        fabLoginA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigo = etCodigoLoginA.getText().toString().trim();
                String password = etPasswordLoginA.getText().toString().trim();
                String tipo = spTipoUsuarioLoginA.getSelectedItem().toString();
                if (!comprobarCampos(codigo, password)) {
                    loginUsuario(codigo, password, tipo);
                } else {
                    Snackbar.make(clUsuarioLoginA, "[!] Comprobar campos vacios", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    public void loginUsuario(String codigo_usuario, String password_usuario, String tipo_usuario)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 0);
        params.put("codigo_usuario", codigo_usuario);
        params.put("password_usuario", password_usuario);
        params.put("tipo_usuario", tipo_usuario);

        try {

            httpclient.post(servidor.urlServidorControlUsuario, params, new JsonHttpResponseHandler() {

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
                            String codigoUsuarioBD = response.getString("Codigo_Usuario");
                            String nombreUsuarioBD = response.getString("Nombre_Usuario");
                            String emailUsuarioBD = response.getString("Email_Usuario");
                            String tipoUsuarioBD = response.getString("Tipo_Usuario");
                            datosSharedPreferences.createLoginSession(codigoUsuarioBD, nombreUsuarioBD, emailUsuarioBD, tipoUsuarioBD);
                            if (tipoUsuarioBD.equals("MEDICO GENERAL")) {
                                startActivity(new Intent(thisActivity, UsuarioMedico.class));
                            } else if (tipoUsuarioBD.equals("NUTRICIONISTA")) {
                                startActivity(new Intent(thisActivity, UsuarioPerfil.class));
                            }
                            thisActivity.finish();
                        } else if (respuesta == 0)
                        {
                            Snackbar.make(clUsuarioLoginA, response.getString("Mensaje_Error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Snackbar.make(clUsuarioLoginA, "Verifique la conexión con el servidor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            });

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean comprobarCampos(String codigo, String password)
    {
        return codigo.equals("") || password.equals("");
    }

    @Override
    public void onBackPressed() { moveTaskToBack(true); }

}
