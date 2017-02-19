package com.brandon.chavez.android.app.sngapp.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;

public class HistorialRegistrar extends Fragment {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public HistorialRegistrar() {}

    public static HistorialRegistrar newInstance() {
        HistorialRegistrar fragment = new HistorialRegistrar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_registrar, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) thisActivity).setSupportActionBar(toolbar);
        if (toolbar != null)
        {
            toolbar.setTitle("Crear historial nuevo");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setHasOptionsMenu(true);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
