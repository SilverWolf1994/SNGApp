package com.brandon.chavez.android.app.sngapp.utilidades;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class WekaDatos {

    private Context context;

    public WekaDatos(Context context)
    {
        this.context = context;
    }

    private Instances wekaLoadData()
    {
        Instances data = null;
        try {
            InputStream inputStream = context.getAssets().open("diagnostico.arff");
            BufferedReader dataset = new BufferedReader(new InputStreamReader(inputStream));
            data = new Instances(dataset);
            dataset.close();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (Exception exception) {
            Log.e("EXCEPTION:\n", exception.getMessage());
        }

        return data;
    }

    public String wekaPredictionEstado(Float imc, int mes)
    {
        String predictionEstado = "";
        try {
            Instances data = wekaLoadData();

            J48 j48 = new J48();
            j48.setUnpruned(true);
            FilteredClassifier filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(j48);
            filteredClassifier.buildClassifier(data);

            Evaluation evaluation = new Evaluation(data);
            evaluation.evaluateModel(filteredClassifier, data);
            //Toast.makeText(context, "Correct: " + evaluation.correct(), Toast.LENGTH_LONG).show();
            //Toast.makeText(context, "Incorrect: " + evaluation.incorrect(), Toast.LENGTH_LONG).show();

            Instance instance = data.instance(data.numAttributes());
            instance.setDataset(data);
            instance.setValue(0, imc);
            instance.setValue(1, mes);

            double prediction = filteredClassifier.classifyInstance(instance);
            predictionEstado = instance.classAttribute().value((int) prediction);

            Log.i("estado:\n", filteredClassifier.toString());

        } catch (Exception exception) {
            Log.e("EXCEPTION:\n", exception.getMessage());
        }

        return predictionEstado;
    }

    public float wekaPredictionIMC(Float imc, int mes, Float adicional, String estado)
    {
        Float predictionIMC = 0f;
        try {
            InputStream inputStream = context.getAssets().open("diagnostico.arff");
            BufferedReader dataset = new BufferedReader(new InputStreamReader(inputStream));
            Instances data = new Instances(dataset);
            dataset.close();
            data.setClassIndex(data.numAttributes() - 4);

            MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
            multilayerPerceptron.buildClassifier(data);

            Instance instance = data.instance(data.numAttributes());
            instance.setDataset(data);
            instance.setValue(0, imc);
            instance.setValue(1, mes);
            instance.setValue(2, adicional);
            instance.setValue(3, estado);

            predictionIMC = (float) multilayerPerceptron.classifyInstance(instance);

            Log.i("numberpredictions:\n", multilayerPerceptron.toString());

        } catch (Exception exception) {
            Log.e("EXCEPTION:\n", exception.getMessage());
        }
        return predictionIMC;
    }
}
