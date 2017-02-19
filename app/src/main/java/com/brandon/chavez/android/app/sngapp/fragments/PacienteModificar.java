package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.UsuarioPerfil;
import com.brandon.chavez.android.app.sngapp.utilidades.AdapterPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class PacienteModificar extends Fragment {

    private static final String CODIGO_PACIENTE = "codigo_paciente";
    private String codigoPaciente;
    private Activity thisActivity;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private EditText etNombrePacienteModificarF, etApellidoPacienteModificarF, etEmailPacienteModificarF;

    private OnFragmentInteractionListener mListener;

    public static PacienteModificar newInstance(String codigoPaciente) {
        PacienteModificar fragment = new PacienteModificar();
        Bundle args = new Bundle();
        args.putString(CODIGO_PACIENTE, codigoPaciente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        { codigoPaciente = getArguments().getString(CODIGO_PACIENTE); }
        thisActivity = getActivity();
        servidor = new Servidor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paciente_modificar, container, false);

        etNombrePacienteModificarF = (EditText) view.findViewById(R.id.etNombrePacienteModificar);
        etApellidoPacienteModificarF = (EditText) view.findViewById(R.id.etApellidoPacienteModificar);
        etEmailPacienteModificarF = (EditText) view.findViewById(R.id.etEmailPacienteModificar);
        Button bGuardarPacienteModificarF = (Button) view.findViewById(R.id.bGuardarPacienteModificar);
        bGuardarPacienteModificarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombrePacienteModificarF.getText().toString();
                String apellido = etApellidoPacienteModificarF.getText().toString();
                String email = etEmailPacienteModificarF.getText().toString().trim();
                actualizarPacientePaciente(codigoPaciente, nombre, apellido, email);
                thisActivity.recreate();
            }
        });
        consultarPaciente(codigoPaciente);

        return view;
    }

    public void consultarPaciente(String codigo_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 2);
        params.put("codigo_paciente", codigo_paciente);

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
                        int respuesta = response.getInt("Success");
                        if (respuesta == 1) {
                            String codigo = response.getString("Codigo_Paciente");
                            String nombre = response.getString("Nombre_Paciente");
                            String apellido = response.getString("Apellido_Paciente");
                            String email = response.getString("Email_Paciente");
                            etNombrePacienteModificarF.setText(nombre);
                            etApellidoPacienteModificarF.setText(apellido);
                            etEmailPacienteModificarF.setText(email);
                        } else if (respuesta == 0) {
                            Toast.makeText(thisActivity, response.getString("Mensaje_Error"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Toast.makeText(thisActivity, "Verifique la conexión con el servidor", Toast.LENGTH_SHORT).show();
                }

            });

        }catch (Exception exception) {
            Toast.makeText(thisActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarPacientePaciente(String codigo_paciente, String nombre_paciente, String apellido_paciente, String email_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 3);
        params.put("codigo_paciente", codigo_paciente);
        params.put("nombre_paciente", nombre_paciente);
        params.put("apellido_paciente", apellido_paciente);
        params.put("email_paciente", email_paciente);

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
                        int respuesta = response.getInt("Success");
                        if (respuesta == 1) {
                            Toast.makeText(thisActivity, "Datos del paciente actualizados", Toast.LENGTH_SHORT).show();
                        } else if (respuesta == 0) {
                            Toast.makeText(thisActivity, response.getString("Mensaje_Error"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Toast.makeText(thisActivity, "Verifique la conexión con el servidor", Toast.LENGTH_SHORT).show();
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
