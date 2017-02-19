package com.brandon.chavez.android.app.sngapp.utilidades;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteGraficasGeneral;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteGraficasHistorial;

public class PagerAdapterPacienteGraficas extends FragmentStatePagerAdapter {

    public PagerAdapterPacienteGraficas(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new PacienteGraficasGeneral();
                break;
            case 1:
                fragment = new PacienteGraficasHistorial();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position)
        {
            case 0:
                title = "GENERAL";
                break;
            case 1:
                title = "PACIENTE";
                break;
        }
        return title;
    }
}
