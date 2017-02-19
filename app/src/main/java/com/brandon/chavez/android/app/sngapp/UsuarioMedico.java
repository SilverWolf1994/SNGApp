package com.brandon.chavez.android.app.sngapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.fragments.PacienteModificar;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteRegistrar;
import com.brandon.chavez.android.app.sngapp.fragments.PacientesListado;
import com.brandon.chavez.android.app.sngapp.fragments.PacientesListadoMedico;
import com.brandon.chavez.android.app.sngapp.utilidades.AdapterPacientesListado;
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

public class UsuarioMedico extends AppCompatActivity implements PacientesListadoMedico.OnFragmentInteractionListener,
        PacienteRegistrar.OnFragmentInteractionListener, PacienteModificar.OnFragmentInteractionListener {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private FloatingActionButton fabAgregarPacienteUserMedicoA;

    private String codigoUsuario;
    private int fragmentActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_medico);
        thisActivity = this;
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        fabAgregarPacienteUserMedicoA = (FloatingActionButton) findViewById(R.id.fabAgregarPacienteUserMedico);

        if (savedInstanceState == null)
        { setFragment(fragmentActual); }

        fabAgregarPacienteUserMedicoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentActual = 1;
                setFragment(fragmentActual);
            }
        });
    }

    private void setFragment(int position)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position)
        {
            case 0:
                if (fabAgregarPacienteUserMedicoA.getVisibility() != View.VISIBLE)
                { fabAgregarPacienteUserMedicoA.setVisibility(View.VISIBLE); }
                PacientesListadoMedico pacientesListadoMedico = new PacientesListadoMedico();
                fragmentTransaction.replace(R.id.content_user_medico, pacientesListadoMedico);
                fragmentTransaction.commit();
                break;
            case 1:
                fabAgregarPacienteUserMedicoA.setVisibility(View.GONE);
                PacienteRegistrar pacienteRegistrar = new PacienteRegistrar();
                fragmentTransaction.replace(R.id.content_user_medico, pacienteRegistrar);
                fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentActual != 0) {
            fragmentActual = 0;
            setFragment(fragmentActual);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usuario_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_informacion:
                /*Intent intentinformacion = new Intent(thisActivity, Informacion.class);
                startActivity(intentinformacion);*/
                return true;
            case R.id.action_cerrar_sesion:
                datosSharedPreferences.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFragmentInteraction(Uri uri){}

}
