package com.brandon.chavez.android.app.sngapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.fragments.PacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;

public class PacienteHistoriales extends Fragment {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public PacienteHistoriales() {}

    public static PacienteHistoriales newInstance() {
        PacienteHistoriales fragment = new PacienteHistoriales();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_paciente_historiales, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) thisActivity).setSupportActionBar(toolbar);
        if (toolbar != null)
        {
            toolbar.setTitle("Historiales");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.paciente_historial, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                Intent intentpacientesListado = new Intent(thisActivity, PacientesListado.class);
                startActivity(intentpacientesListado);
                break;

            case R.id.action_agregar:
                /*Intent intentinformacion = new Intent(thisActivity, Informacion.class);
                startActivity(intentinformacion);*/
                break;
        }
        return super.onOptionsItemSelected(item);
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
