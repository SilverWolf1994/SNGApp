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
import android.widget.EditText;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.ChartValueFormatter;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PacienteGraficasHistorial extends Fragment {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private EditText etcodigoPacienteGraficasHistorialF;
    private Button bGraficoPacienteGraficasHistorialF;
    private LineChart lcPacienteGraficasHistorialF;
    private String codigoUsuario;
    private ArrayList<Float> array_historial_imc = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public PacienteGraficasHistorial() {}

    public static PacienteGraficasHistorial newInstance(String param1, String param2) {
        PacienteGraficasHistorial fragment = new PacienteGraficasHistorial();
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
        View view = inflater.inflate(R.layout.fragment_paciente_graficas_historial, container, false);
        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        etcodigoPacienteGraficasHistorialF = (EditText) view.findViewById(R.id.etcodigoPacienteGraficasHistorial);
        bGraficoPacienteGraficasHistorialF = (Button) view.findViewById(R.id.bGraficoPacienteGraficasHistorial);
        lcPacienteGraficasHistorialF = (LineChart) view.findViewById(R.id.lcPacienteGraficasHistorial);

        bGraficoPacienteGraficasHistorialF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                array_historial_imc.clear();
                String codigoPaciente = etcodigoPacienteGraficasHistorialF.getText().toString().trim();
                historialPacienteBD(codigoUsuario, codigoPaciente);
                lcPacienteGraficasHistorialF.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public void historialPacienteBD(String codigo_usuario, String codigo_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 7);
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

                        if (response.getJSONArray("Historiales").length() > 0)
                        {
                            for (int i = 0; i < response.getJSONArray("Historiales").length(); i++)
                            {
                                Float imc_historial = Float.parseFloat(response.getJSONArray("Historiales").getJSONObject(i).getString("Imc_Historial"));
                                array_historial_imc.add(imc_historial);
                            }
                            valoresGraficas();
                        } else {
                            Toast.makeText(thisActivity, "No se encontro historial para este usuario", Toast.LENGTH_SHORT).show();
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

    public void cargarGrafica(List<ILineDataSet> dataSets)
    {
        String[] valuesX = new String[] {"MES1", "MES2", "MES3", "MES4", "MES5", "MES6", "MES7", "MES8", "MES9"} ;
        YAxis yAxis = lcPacienteGraficasHistorialF.getAxisLeft();
        XAxis xAxis = lcPacienteGraficasHistorialF.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(9);
        xAxis.setValueFormatter(new ChartValueFormatter(valuesX));

        LineData data = new LineData(dataSets);
        lcPacienteGraficasHistorialF.setData(data);
        lcPacienteGraficasHistorialF.setDescription(null);
        lcPacienteGraficasHistorialF.animateXY(1000, 0);
        lcPacienteGraficasHistorialF.invalidate();
    }

    public void valoresGraficas()
    {
        List<Entry> dataHistorial = new ArrayList<>();

        for (int iHistorial = 0; iHistorial < array_historial_imc.size(); iHistorial++)
        {
            dataHistorial.add(new Entry(iHistorial, array_historial_imc.get(iHistorial)));
        }

        LineDataSet ldsHistorial = new LineDataSet(dataHistorial, "Historial");
        ldsHistorial.setAxisDependency(YAxis.AxisDependency.LEFT);
        ldsHistorial.setColor(Color.parseColor("#4FC3F7"));
        ldsHistorial.setCircleColor(Color.parseColor("#4FC3F7"));

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(ldsHistorial);

        cargarGrafica(dataSets);
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
