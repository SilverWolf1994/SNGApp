package com.brandon.chavez.android.app.sngapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.PagerAdapterPacienteGraficas;

import java.util.ArrayList;
import java.util.List;

public class PacienteGraficas extends Fragment {

    private OnFragmentInteractionListener mListener;

    public PacienteGraficas() {}

    public static PacienteGraficas newInstance() {
        PacienteGraficas fragment = new PacienteGraficas();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paciente_graficas, container, false);

        ViewPager viewPagerPacienteGraficasF = (ViewPager) view.findViewById(R.id.viewPagerPacienteGraficas);
        TabLayout tabLayoutPacienteGraficasF = (TabLayout) view.findViewById(R.id.tabLayoutPacienteGraficas);

        FragmentManager fragmentManager = getFragmentManager();
        PagerAdapterPacienteGraficas pagerAdapterPacienteGraficas = new PagerAdapterPacienteGraficas(fragmentManager);
        viewPagerPacienteGraficasF.setAdapter(pagerAdapterPacienteGraficas);
        viewPagerPacienteGraficasF.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutPacienteGraficasF));
        tabLayoutPacienteGraficasF.setupWithViewPager(viewPagerPacienteGraficasF);

        return view;
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
