package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.AdapterPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.ChartValueFormatter;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosPacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.brandon.chavez.android.app.sngapp.utilidades.WekaDatos;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class PacientePredecirEstado extends Fragment {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private WekaDatos wekaDatos;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private EditText etCodigoPacientePredecirEstadoF;
    private Button bPredecirEstadoPacientePredecirEstadoF;
    private LineChart lcGraficaIdealPacientePredecirEstadoF;
    private ArrayList<Float> array_historial_imc = new ArrayList<>();
    private ArrayList<Float> array_prediccion_imc = new ArrayList<>();
    private ArrayList<Float> array_ideal_imc = new ArrayList<>();

    private String codigoUsuario;

    private OnFragmentInteractionListener mListener;

    public PacientePredecirEstado() {}

    public static PacientePredecirEstado newInstance() {
        PacientePredecirEstado fragment = new PacientePredecirEstado();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        wekaDatos = new WekaDatos(thisActivity);
        servidor = new Servidor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paciente_predecir_estado, container, false);
        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);

        etCodigoPacientePredecirEstadoF = (EditText) view.findViewById(R.id.etCodigoPacientePredecirEstado);
        bPredecirEstadoPacientePredecirEstadoF = (Button) view.findViewById(R.id.bPredecirEstadoPacientePredecirEstado);
        lcGraficaIdealPacientePredecirEstadoF = (LineChart) view.findViewById(R.id.lcGraficaIdealPacientePredecirEstado);

        bPredecirEstadoPacientePredecirEstadoF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                array_historial_imc.clear();
                array_prediccion_imc.clear();
                array_ideal_imc.clear();
                String codigoPaciente = etCodigoPacientePredecirEstadoF.getText().toString();
                historialPacientesBD(codigoUsuario, codigoPaciente);
                lcGraficaIdealPacientePredecirEstadoF.setVisibility(View.VISIBLE);


                //Float altura = (float) Math.round(Float.parseFloat(etAlturaPacientePredecirEstado.getText().toString()) * 100f) / 100f;

                /*String prediccion = wekaDatos.wekaPrediction(imc);
                prediccionColor(prediccion);

                tvImcEstadoPacientePredecirEstadoF.setText(String.valueOf(imc));
                tvPrediccionEstadoPacientePredecirEstadoF.setText(prediccion);*/

            }
        });

        return view;
    }

    public void historialPacientesBD(String codigo_usuario, String codigo_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 4);
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
                            int auxiliar_prediccion = 0;
                            Float ultimo_imc = 0f;
                            int ultimo_mes = 0;
                            for (int i = 0; i < response.getJSONArray("Historiales").length(); i++)
                            {
                                Float imc_historial = Float.parseFloat(response.getJSONArray("Historiales").getJSONObject(i).getString("Imc_Historial"));
                                int mes_historial = Integer.parseInt(response.getJSONArray("Historiales").getJSONObject(i).getString("Mes_Historial"));

                                auxiliar_prediccion++;
                                ultimo_imc = imc_historial;
                                ultimo_mes = mes_historial;

                                array_historial_imc.add(ultimo_imc);
                                array_prediccion_imc.add(ultimo_imc);

                                if (i == 0)
                                {
                                    array_ideal_imc.add(Float.parseFloat(response.getJSONArray("Historiales").getJSONObject(i).getString("Imc_Historial")));
                                    array_ideal_imc.add(Float.parseFloat(response.getJSONArray("Historiales").getJSONObject(i).getString("Altura_Historial")));
                                    array_ideal_imc.add(Float.parseFloat(response.getJSONArray("Historiales").getJSONObject(i).getString("Peso_Historial")));
                                }
                            }
                            predecirIMC(auxiliar_prediccion, ultimo_imc, ultimo_mes);
                        } else {
                            //Snackbar.make(clUsuarioLoginA, No se encontraron pacientes a listar, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }catch (JSONException jsonexception) {
                        progressDialog.dismiss();
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

    public void predecirIMC(int auxiliar_prediccion, Float ultimo_imc, int ultimo_mes)
    {
        /* Cargamos el array_prediccion_imc usando weka para predecir los proximos datos */
        int numero_recorridos = 9 - auxiliar_prediccion;
        for (int recorrido = 0; recorrido < numero_recorridos; recorrido++)
        {
            String estado_classify = wekaDatos.wekaPredictionEstado(ultimo_imc, ultimo_mes);
            Float peso_adicional = pesoAdicional(ultimo_mes, estado_classify);
            ultimo_imc = (float) Math.round(wekaDatos.wekaPredictionIMC(ultimo_imc, ultimo_mes, peso_adicional, estado_classify) * 100f) / 100f;
            ultimo_mes++;
            array_prediccion_imc.add(ultimo_imc);
        }

        /* Cargamos el array_ideal_imc utilizando el metodo */
        Float imc_ideal = array_ideal_imc.get(0);
        Float altura_ideal = array_ideal_imc.get(1);
        Float peso_ideal = array_ideal_imc.get(2);
        array_ideal_imc.clear(); /* Limpiamos el array_ideal_imc ya que este solo tendra datos imc */
        array_ideal_imc.add(imc_ideal);
        String primer_estado = wekaDatos.wekaPredictionEstado(imc_ideal, 1);
        for (int mes_ideal = 1; mes_ideal < 9; mes_ideal++)
        {
            peso_ideal = peso_ideal + pesoAdicional(mes_ideal, primer_estado);
            imc_ideal = calcularImc(peso_ideal, altura_ideal);
            array_ideal_imc.add(imc_ideal);
        }
        /* Se llama al metodo (valoresGraficas) que cargara los datasets de las distintas graficas */
        valoresGraficas();
    }

    public void cargarGrafica(List<ILineDataSet> dataSets)
    {
        String[] valuesX = new String[] {"MES1", "MES2", "MES3", "MES4", "MES5", "MES6", "MES7", "MES8", "MES9"} ;
        YAxis yAxis = lcGraficaIdealPacientePredecirEstadoF.getAxisLeft();
        XAxis xAxis = lcGraficaIdealPacientePredecirEstadoF.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(9);
        xAxis.setValueFormatter(new ChartValueFormatter(valuesX));

        LineData data = new LineData(dataSets);
        lcGraficaIdealPacientePredecirEstadoF.setData(data);
        lcGraficaIdealPacientePredecirEstadoF.setDescription(null);
        lcGraficaIdealPacientePredecirEstadoF.animateXY(1000, 0);
        lcGraficaIdealPacientePredecirEstadoF.invalidate();
    }

    public void valoresGraficas()
    {
        List<Entry> dataHistorial = new ArrayList<>();
        List<Entry> dataPrediccion = new ArrayList<>();
        List<Entry> dataIdeal = new ArrayList<>();

        for (int iHistorial = 0; iHistorial < array_historial_imc.size(); iHistorial++)
        {
            dataHistorial.add(new Entry(iHistorial, array_historial_imc.get(iHistorial)));
        }

        for (int iPrediccion = 0; iPrediccion < array_prediccion_imc.size(); iPrediccion++)
        {
            dataPrediccion.add(new Entry(iPrediccion, array_prediccion_imc.get(iPrediccion)));
        }

        for (int iIdeal = 0; iIdeal < array_ideal_imc.size(); iIdeal++)
        {
            dataIdeal.add(new Entry(iIdeal, array_ideal_imc.get(iIdeal)));
        }

        LineDataSet ldsHistorial = new LineDataSet(dataHistorial, "Historial");
        ldsHistorial.setAxisDependency(YAxis.AxisDependency.LEFT);
        ldsHistorial.setColor(Color.parseColor("#4FC3F7"));
        ldsHistorial.setCircleColor(Color.parseColor("#4FC3F7"));

        LineDataSet ldsPrediccion = new LineDataSet(dataPrediccion, "Predicción");
        ldsPrediccion.setAxisDependency(YAxis.AxisDependency.LEFT);
        ldsPrediccion.setColor(Color.parseColor("#AB47BC"));
        ldsPrediccion.setCircleColor(Color.parseColor("#AB47BC"));

        LineDataSet ldsIdeal = new LineDataSet(dataIdeal, "Ideal");
        ldsIdeal.setAxisDependency(YAxis.AxisDependency.LEFT);
        ldsIdeal.setColor(Color.parseColor("#69F0AE"));
        ldsIdeal.setCircleColor(Color.parseColor("#69F0AE"));

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(ldsPrediccion);//Mostrar primero el historial del paciente.
        dataSets.add(ldsHistorial);//Mostrar la prediccion en los siguientes meses.
        dataSets.add(ldsIdeal);//Mostrar el estado ideal en los 9 meses.

        cargarGrafica(dataSets);
    }

    public Float pesoAdicional(int mes, String estado)
    {
        Float adicional = 0f;

        if (estado.equals("BAJO"))
        {
            switch (mes)
            {
                case 1:
                    adicional = 0.5f;
                    break;
                case 2:
                    adicional = 1f;
                    break;
                case 3:
                    adicional = 1f;
                    break;
                case 4:
                    adicional = 1.5f;
                    break;
                case 5:
                    adicional = 1.5f;
                    break;
                case 6:
                    adicional = 2f;
                    break;
                case 7:
                    adicional = 2f;
                    break;
                case 8:
                    adicional = 2.5f;
                    break;
                case 9:
                    adicional = 2f;
                    break;
            }
        }
        else if (estado.equals("NORMAL"))
        {
            switch (mes)
            {
                case 1:
                    adicional = 0f;
                    break;
                case 2:
                    adicional = 0.5f;
                    break;
                case 3:
                    adicional = 1f;
                    break;
                case 4:
                    adicional = 1f;
                    break;
                case 5:
                    adicional = 1f;
                    break;
                case 6:
                    adicional = 2f;
                    break;
                case 7:
                    adicional = 2f;
                    break;
                case 8:
                    adicional = 2f;
                    break;
                case 9:
                    adicional = 2f;
                    break;
            }
        }
        else if (estado.equals("SOBREPESO"))
        {
            switch (mes)
            {
                case 1:
                    adicional = 0f;
                    break;
                case 2:
                    adicional = 0f;
                    break;
                case 3:
                    adicional = 0.5f;
                    break;
                case 4:
                    adicional = 1f;
                    break;
                case 5:
                    adicional = 1f;
                    break;
                case 6:
                    adicional = 1.5f;
                    break;
                case 7:
                    adicional = 1.5f;
                    break;
                case 8:
                    adicional = 2f;
                    break;
                case 9:
                    adicional = 1.5f;
                    break;
            }
        }
        else if (estado.equals("OBESIDAD"))
        {
            switch (mes)
            {
                case 1:
                    adicional = 0f;
                    break;
                case 2:
                    adicional = 0f;
                    break;
                case 3:
                    adicional = 0f;
                    break;
                case 4:
                    adicional = 0.5f;
                    break;
                case 5:
                    adicional = 1f;
                    break;
                case 6:
                    adicional = 1f;
                    break;
                case 7:
                    adicional = 1.5f;
                    break;
                case 8:
                    adicional = 2.5f;
                    break;
                case 9:
                    adicional = 1f;
                    break;
            }
        }

        return adicional;
    }

    public Float calcularImc(Float peso, Float altura)
    {
        return Math.round(peso/(altura * altura) * 100f) / 100f;
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
