package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.dialogs.PacienteHistorial;
import com.brandon.chavez.android.app.sngapp.dialogs.PacienteHistoriales;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PacientesListado extends Fragment implements AdapterPacientesListado.OnPacienteSelected {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private RecyclerView rvPacientesListadoF;

    private DatosPacientesListado datosPacientesListado;
    private List<DatosPacientesListado> datosPacientesListados = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private AdapterPacientesListado adapterPacientesListado;

    private String codigoUsuario;

    private OnFragmentInteractionListener mListener;

    public static PacientesListado newInstance() {
        PacientesListado fragment = new PacientesListado();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pacientes_listado, container, false);
        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        rvPacientesListadoF = (RecyclerView) view.findViewById(R.id.rvPacientesListado);

        listadoPacientesBD(codigoUsuario);

        return view;
    }

    @Override
    public void onPacienteClick(DatosPacientesListado datosPacientesListado)
    {
        Intent intentpacientehistorial = new Intent(thisActivity, PacienteHistorial.class);
        intentpacientehistorial.putExtra("CODIGO_PACIENTE", datosPacientesListado.getCodigoPaciente());
        startActivity(intentpacientehistorial);
    }

    public void listadoPacientesBD(String codigo_usuario)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 5);
        params.put("codigo_usuario", codigo_usuario);

        try{

            httpclient.post(servidor.urlServidorControlPaciente, params, new JsonHttpResponseHandler() {

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

                        if (response.getJSONArray("Pacientes").length() > 0) {
                            for (int i = 0; i < response.getJSONArray("Pacientes").length(); i++)
                            {
                                datosPacientesListado = new DatosPacientesListado();
                                datosPacientesListado.setCodigoPaciente(response.getJSONArray("Pacientes").getJSONObject(i).getString("Codigo_Paciente"));
                                datosPacientesListado.setNombrePaciente(response.getJSONArray("Pacientes").getJSONObject(i).getString("Nombre_Paciente"));
                                datosPacientesListado.setApellidoPaciente(response.getJSONArray("Pacientes").getJSONObject(i).getString("Apellido_Paciente"));
                                datosPacientesListado.setImagenPaciente(response.getJSONArray("Pacientes").getJSONObject(i).getString("Imagen_Paciente"));
                                datosPacientesListados.add(datosPacientesListado);
                            }
                            adapterPacientesListado = new AdapterPacientesListado(PacientesListado.this, thisActivity, datosPacientesListados, PacientesListado.this);
                            staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                            rvPacientesListadoF.setLayoutManager(staggeredGridLayoutManager);
                            rvPacientesListadoF.setHasFixedSize(true);
                            rvPacientesListadoF.setAdapter(adapterPacientesListado);

                        } else {
                            //Snackbar.make(clUsuarioLoginA, No se encontraron pacientes a listar, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
