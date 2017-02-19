package com.brandon.chavez.android.app.sngapp.utilidades;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.UsuarioMedico;
import com.brandon.chavez.android.app.sngapp.UsuarioPerfil;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteModificar;
import com.brandon.chavez.android.app.sngapp.fragments.PacienteRegistrar;
import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AdapterPacientesListado extends RecyclerView.Adapter<AdapterPacientesListado.ViewHolder> {

    private Context context;
    private Fragment fragment;
    private List<DatosPacientesListado> lDatosPacientesListado;
    private OnPacienteSelected onPacienteSelected;
    private Rect rect = new Rect(50, 50, 50, 50);
    private Servidor servidor = new Servidor();
    private ProgressDialog progressDialog;
    private String urlGetImages = servidor.urlServidorControlPacienteImagen;

    public AdapterPacientesListado(Fragment fragment, Context context, List<DatosPacientesListado> lDatosPacientesListado, OnPacienteSelected onPacienteSelected)
    {
        this.fragment = fragment;
        this.context = context;
        this.lDatosPacientesListado = lDatosPacientesListado;
        try{
            this.onPacienteSelected = onPacienteSelected;
        }catch (ClassCastException e){
            throw new ClassCastException("must implement OnPacienteSelected");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cvItemPacientesListadoAdap;
        public TextView tvCodigoItemPacientesListadoAdap;
        public TextView tvNombreItemPacientesListadoAdap;
        public TextView tvApellidoItemPacientesListadoAdap;
        public SmartImageView sivImagenItemPacientesListadoAdap;
        public ImageView ivOverflowPacientesListadoAdap;

        public ViewHolder(View view)
        {
            super(view);
            cvItemPacientesListadoAdap = (CardView) view.findViewById(R.id.cvItemPacientesListado);
            tvCodigoItemPacientesListadoAdap = (TextView) view.findViewById(R.id.tvCodigoItemPacientesListado);
            tvNombreItemPacientesListadoAdap = (TextView) view.findViewById(R.id.tvNombreItemPacientesListado);
            tvApellidoItemPacientesListadoAdap = (TextView) view.findViewById(R.id.tvApellidoItemPacientesListado);
            sivImagenItemPacientesListadoAdap = (SmartImageView) view.findViewById(R.id.sivImagenItemPacientesListado);
            ivOverflowPacientesListadoAdap = (ImageView) view.findViewById(R.id.ivOverflowPacientesListado);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pacientes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        final DatosPacientesListado datosPacientesListado = lDatosPacientesListado.get(position);
        holder.tvCodigoItemPacientesListadoAdap.setText(datosPacientesListado.getCodigoPaciente());
        holder.tvNombreItemPacientesListadoAdap.setText(datosPacientesListado.getNombrePaciente());
        holder.tvApellidoItemPacientesListadoAdap.setText(datosPacientesListado.getApellidoPaciente());
        holder.sivImagenItemPacientesListadoAdap.setImageUrl(urlGetImages + datosPacientesListado.getImagenPaciente(), rect);
        holder.cvItemPacientesListadoAdap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onPacienteSelected.onPacienteClick(datosPacientesListado);
            }
        });
        holder.ivOverflowPacientesListadoAdap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuOpciones(holder.ivOverflowPacientesListadoAdap, datosPacientesListado);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return lDatosPacientesListado.size();
    }

    public interface OnPacienteSelected
    {
        void onPacienteClick(DatosPacientesListado datosPacientesListado);
    }

    private void showMenuOpciones(View view, DatosPacientesListado datosPacientesListado)
    {
        String codigoPaciente = datosPacientesListado.getCodigoPaciente();
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.pacientes_listado_opciones, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MenuOpcionesPacienteListener(codigoPaciente));
        popupMenu.show();
    }

    private class MenuOpcionesPacienteListener implements PopupMenu.OnMenuItemClickListener {
        private String codigoPaciente;
        private MenuOpcionesPacienteListener(String codigo_paciente)
        { codigoPaciente = codigo_paciente; }
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_modificar_paciente:
                    PacienteModificar pacienteModificar = PacienteModificar.newInstance(codigoPaciente);
                    //android.support.v4.app.FragmentManager fragmentManager = fragment.getFragmentManager();
                    android.support.v4.app.FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.contentFragments, pacienteModificar).commit();
                    return true;
                case R.id.action_eliminar_paciente:
                    eliminarPaciente(codigoPaciente);
                    return true;
                default:
            }
            return false;
        }
    }

    private void eliminarPaciente(String codigo_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("opcion", 4);
        params.put("codigo_paciente", codigo_paciente);

        try {

            httpclient.post(servidor.urlServidorControlPaciente, params, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    progressDialog = new ProgressDialog(context);
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
                            ((Activity) context).recreate();
                        } else if (respuesta == 0)
                        {
                            Toast.makeText(context, response.getString("Mensaje_Error"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(context, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Verifique la conexión con el servidor", Toast.LENGTH_SHORT).show();
                }

            });

        } catch (Exception exception) {
            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

}
