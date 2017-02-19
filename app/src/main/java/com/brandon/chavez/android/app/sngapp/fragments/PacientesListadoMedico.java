package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
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

public class PacientesListadoMedico extends Fragment implements AdapterPacientesListado.OnPacienteSelected {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private RecyclerView rvMedicoPacientesListadoF;

    private DatosPacientesListado datosPacientesListado;
    private List<DatosPacientesListado> datosPacientesListados = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AdapterPacientesListado adapterPacientesListado;

    private String codigoUsuario;

    private OnFragmentInteractionListener mListener;

    public static PacientesListadoMedico newInstance() {
        PacientesListadoMedico fragment = new PacientesListadoMedico();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();

        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pacientes_listado_medico, container, false);
        rvMedicoPacientesListadoF = (RecyclerView) view.findViewById(R.id.rvMedicoPacientesListado);
        listadoPacientesBD(codigoUsuario);
        return view;
    }

    @Override
    public void onPacienteClick(DatosPacientesListado datosPacientesListado)
    {
        showDialog(datosPacientesListado.getCodigoPaciente());
    }

    private void showDialog(final String codigo_paciente)
    {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(thisActivity);
        View dialogView = layoutInflaterAndroid.inflate(R.layout.dialog_medico_paciente, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(thisActivity);
        alertDialogBuilderUserInput.setView(dialogView);
        final EditText etDialogCodigoNutricionistaA = (EditText) dialogView.findViewById(R.id.etDialogCodigoNutricionista);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        String codigo_usuario = etDialogCodigoNutricionistaA.getText().toString().trim();
                        if (codigo_usuario.equals("")) {
                            Toast.makeText(thisActivity, "Campo no puede ser vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.cancel();
                            asignarPacienteNutricionistaBD(codigo_paciente, codigo_usuario);
                        }
                    }
                })

                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void asignarPacienteNutricionistaBD(String codigo_paciente, String codigo_usuario)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 7);
        params.put("codigo_paciente", codigo_paciente);
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

                        int respuesta = response.getInt("Success");
                        if (respuesta == 1)
                        {
                            Toast.makeText(thisActivity, "Paciente asignado!", Toast.LENGTH_SHORT).show();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.detach(PacientesListadoMedico.this).attach(PacientesListadoMedico.this).commit();
                        }
                        else if (respuesta == 0)
                        {
                            Toast.makeText(thisActivity, response.getString("Mensaje_Error"), Toast.LENGTH_LONG).show();
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
                            adapterPacientesListado = new AdapterPacientesListado(PacientesListadoMedico.this, thisActivity, datosPacientesListados, PacientesListadoMedico.this);
                            linearLayoutManager = new LinearLayoutManager(thisActivity);
                            rvMedicoPacientesListadoF.setLayoutManager(linearLayoutManager);
                            rvMedicoPacientesListadoF.setHasFixedSize(true);
                            rvMedicoPacientesListadoF.setAdapter(adapterPacientesListado);

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
