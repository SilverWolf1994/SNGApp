package com.brandon.chavez.android.app.sngapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.brandon.chavez.android.app.sngapp.dialogs.HistorialRegistrar;
import com.brandon.chavez.android.app.sngapp.dialogs.PacienteHistoriales;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteGraficasGeneral;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteGraficasHistorial;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteModificar;
import com.brandon.chavez.android.app.sngapp.fragments.PacientePredecirEstado;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteGraficas;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteRegistrar;
import com.brandon.chavez.android.app.sngapp.fragments.PacientesListado;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;

public class UsuarioPerfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PacienteHistoriales.OnFragmentInteractionListener, HistorialRegistrar.OnFragmentInteractionListener,
        PacientesListado.OnFragmentInteractionListener, PacienteRegistrar.OnFragmentInteractionListener,
        PacientePredecirEstado.OnFragmentInteractionListener, PacienteGraficas.OnFragmentInteractionListener,
        PacienteGraficasHistorial.OnFragmentInteractionListener, PacienteGraficasGeneral.OnFragmentInteractionListener,
        PacienteModificar.OnFragmentInteractionListener {

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private int fragmentActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_perfil);
        thisActivity = this;
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
        {
            setFragment(fragmentActual);
        }
    }

    private void setFragment(int position)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position)
        {
            case 0:
                navigationView.getMenu().getItem(fragmentActual).setChecked(true);
                PacientesListado pacientesListado = new PacientesListado();
                fragmentTransaction.replace(R.id.contentFragments, pacientesListado);
                fragmentTransaction.commit();
                break;
            case 1:
                navigationView.getMenu().getItem(fragmentActual).setChecked(true);
                PacienteRegistrar pacienteRegistrar = new PacienteRegistrar();
                fragmentTransaction.replace(R.id.contentFragments, pacienteRegistrar);
                fragmentTransaction.commit();
                break;
            case 2:
                navigationView.getMenu().getItem(fragmentActual).setChecked(true);
                PacientePredecirEstado pacientePredecirEstado = new PacientePredecirEstado();
                fragmentTransaction.replace(R.id.contentFragments, pacientePredecirEstado);
                fragmentTransaction.commit();
                break;
            case 3:
                navigationView.getMenu().getItem(fragmentActual).setChecked(true);
                PacienteGraficas pacienteGraficas = new PacienteGraficas();
                fragmentTransaction.replace(R.id.contentFragments, pacienteGraficas);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentActual != 0) {
                fragmentActual = 0;
                setFragment(fragmentActual);
            } else {
                moveTaskToBack(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usuario_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_informacion:
                /*Intent intentinformacion = new Intent(thisActivity, Informacion.class);
                startActivity(intentinformacion);*/
                return true;
            case R.id.action_cerrar_sesion:
                datosSharedPreferences.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_listado_pacientes:
                fragmentActual = 0;
                setFragment(fragmentActual);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_registrar_paciente:
                fragmentActual = 1;
                setFragment(fragmentActual);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_prediccion_paciente:
                fragmentActual = 2;
                setFragment(fragmentActual);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_clasificacion_graficas:
                fragmentActual = 3;
                setFragment(fragmentActual);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    public void onFragmentInteraction(Uri uri){}

}
