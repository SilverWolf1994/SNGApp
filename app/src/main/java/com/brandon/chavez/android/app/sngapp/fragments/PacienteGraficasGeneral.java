package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.ChartValueFormatter;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PacienteGraficasGeneral extends Fragment {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private BarChart bcPacienteGraficasGeneralF;
    private String codigoUsuario;

    private OnFragmentInteractionListener mListener;

    public PacienteGraficasGeneral() {}

    public static PacienteGraficasGeneral newInstance() {
        PacienteGraficasGeneral fragment = new PacienteGraficasGeneral();
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
        View view = inflater.inflate(R.layout.fragment_paciente_graficas_general, container, false);
        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        bcPacienteGraficasGeneralF = (BarChart) view.findViewById(R.id.bcPacienteGraficasGeneral);
        Button bCargarGraficaGeneralF = (Button) view.findViewById(R.id.bCargarGraficaGeneral);
        bCargarGraficaGeneralF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graficaGeneralBD(codigoUsuario);
            }
        });
        graficaGeneralBD(codigoUsuario);

        return view;
    }

    private void cargarGrafica(int bajo, int normal, int sobrepeso, int obesidad)
    {
        BarData data = new BarData(getDataset(bajo, normal, sobrepeso, obesidad));
        data.setBarWidth(0.9f);
        bcPacienteGraficasGeneralF.setData(data);
        bcPacienteGraficasGeneralF.setDescription(null);
        bcPacienteGraficasGeneralF.animateXY(2000, 2000);
        bcPacienteGraficasGeneralF.setFitBars(true);
        bcPacienteGraficasGeneralF.invalidate();

        String[] valuesX = new String[] {"BAJO", "NORMAL", "SOBREPESO", "OBESIDAD"} ;

        YAxis yAxis = bcPacienteGraficasGeneralF.getAxisLeft();
        XAxis xAxis = bcPacienteGraficasGeneralF.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new ChartValueFormatter(valuesX));
    }

    private BarDataSet getDataset(int bajo, int normal, int sobrepeso, int obesidad)
    {
        List<BarEntry> dataValue = new ArrayList<>();

        BarEntry valueBajo = new BarEntry(0f, bajo);
        BarEntry valueNormal = new BarEntry(1f, normal);
        BarEntry valueSobrepeso = new BarEntry(2f, sobrepeso);
        BarEntry valueObesidad = new BarEntry(3f, obesidad);

        dataValue.add(valueBajo);
        dataValue.add(valueNormal);
        dataValue.add(valueSobrepeso);
        dataValue.add(valueObesidad);

        BarDataSet barDataSet = new BarDataSet(dataValue, "");
        barDataSet.setColors(new int[] {Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});

        return barDataSet;
    }

    public void graficaGeneralBD(String codigo_usuario)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 6);
        params.put("codigo_usuario", codigo_usuario);

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
                        int bajo = 0; int normal = 0; int sobrepeso = 0; int obesidad = 0;
                        if (response.getJSONArray("Casos").length() > 0)
                        {
                            for (int i = 0; i < response.getJSONArray("Casos").length(); i++)
                            {
                                if (response.getJSONArray("Casos").getJSONObject(i).getString("Nombre_Casos").equals("BAJO")) {
                                    bajo = Integer.parseInt(response.getJSONArray("Casos").getJSONObject(i).getString("Numero_Casos"));
                                } else if(response.getJSONArray("Casos").getJSONObject(i).getString("Nombre_Casos").equals("NORMAL")) {
                                    normal = Integer.parseInt(response.getJSONArray("Casos").getJSONObject(i).getString("Numero_Casos"));
                                } else if(response.getJSONArray("Casos").getJSONObject(i).getString("Nombre_Casos").equals("SOBREPESO")) {
                                    sobrepeso = Integer.parseInt(response.getJSONArray("Casos").getJSONObject(i).getString("Numero_Casos"));
                                } else if(response.getJSONArray("Casos").getJSONObject(i).getString("Nombre_Casos").equals("OBESIDAD")) {
                                    obesidad = Integer.parseInt(response.getJSONArray("Casos").getJSONObject(i).getString("Numero_Casos"));
                                }
                            }
                            cargarGrafica(bajo, normal, sobrepeso, obesidad);
                        } else {
                            Toast.makeText(thisActivity, "No hay casos registrados", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException jsonexception) {
                        progressDialog.dismiss();
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
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
