package com.brandon.chavez.android.app.sngapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.brandon.chavez.android.app.sngapp.R;
import com.brandon.chavez.android.app.sngapp.utilidades.DatosSharedPreferences;
import com.brandon.chavez.android.app.sngapp.utilidades.Servidor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import cz.msebera.android.httpclient.Header;

public class PacienteRegistrar extends Fragment {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_SELECT_IMAGE_REQUEST_CODE = 200;
    private static final int CAMERA_CROP_IMAGE_REQUEST_CODE = 300;
    private static final File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SNGApp");
    private String random_nombre;
    private String imagen_nombre;
    private String imagen_directorio;
    private String imagen_crop;
    private File file_imagen;
    private File file_crop;
    private Uri output;
    private Uri selectImage;

    private Activity thisActivity;
    private DatosSharedPreferences datosSharedPreferences;
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private CoordinatorLayout clPacienteRegistrarF;
    private ImageView ivImagenPacienteRegistrarF;
    private EditText etCodigoPacienteRegistrarF, etNombrePacienteRegistrarF,
            etApellidoPacienteRegistrarF, etEmailPacienteRegistrarF;
    private FloatingActionButton fabImagenPacienteRegistrarF, fabGuardarPacienteRegistrarF;

    private String codigoUsuario;

    private OnFragmentInteractionListener mListener;

    public static PacienteRegistrar newInstance() {
        PacienteRegistrar fragment = new PacienteRegistrar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
        datosSharedPreferences = new DatosSharedPreferences(thisActivity);
        servidor = new Servidor();

        datosSharedPreferences.checkLogin();
        HashMap<String, String> datosUsuario = datosSharedPreferences.getUserDetails();
        codigoUsuario = datosUsuario.get(DatosSharedPreferences.CODIGO_USUARIO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paciente_registrar, container, false);

        clPacienteRegistrarF = (CoordinatorLayout) view.findViewById(R.id.clUsuarioPerfil);
        ivImagenPacienteRegistrarF = (ImageView) view.findViewById(R.id.ivImagenPacienteRegistrar);
        etCodigoPacienteRegistrarF = (EditText) view.findViewById(R.id.etCodigoPacienteRegistrar);
        etNombrePacienteRegistrarF = (EditText) view.findViewById(R.id.etNombrePacienteRegistrar);
        etApellidoPacienteRegistrarF = (EditText) view.findViewById(R.id.etApellidoPacienteRegistrar);
        etEmailPacienteRegistrarF = (EditText) view.findViewById(R.id.etEmailPacienteRegistrar);
        fabImagenPacienteRegistrarF = (FloatingActionButton) view.findViewById(R.id.fabImagenPacienteRegistrar);
        fabGuardarPacienteRegistrarF = (FloatingActionButton) view.findViewById(R.id.fabGuardarPacienteRegistrar);

        fabImagenPacienteRegistrarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSeleccionarImagen();
            }
        });

        fabGuardarPacienteRegistrarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPacienteBD(codigoUsuario, etCodigoPacienteRegistrarF.getText().toString(),
                        etNombrePacienteRegistrarF.getText().toString(), etApellidoPacienteRegistrarF.getText().toString(),
                        etEmailPacienteRegistrarF.getText().toString(), obtenerFecha(), obtenerFecha(), obtenerHora(),
                        imagen_crop, file_crop);
            }
        });

        return view;
    }

    public void registrarPacienteBD(String codigo_usuario, String codigo_paciente, String nombre_paciente,
                                    String apellido_paciente, String email_paciente, String fecha_nacimiento_paciente,
                                    String fecha_registro_paciente, String hora_registro_paciente, String imagen_paciente,
                                    File media_imagen_paciente)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        try {
            params.put("opcion", 1);
            params.put("codigo_usuario", codigo_usuario);
            params.put("codigo_paciente", codigo_paciente);
            params.put("nombre_paciente", nombre_paciente);
            params.put("apellido_paciente", apellido_paciente);
            params.put("email_paciente", email_paciente);
            params.put("fecha_nacimiento_paciente", fecha_nacimiento_paciente);
            params.put("fecha_registro_paciente", fecha_registro_paciente);
            params.put("hora_registro_paciente", hora_registro_paciente);
            params.put("imagen_paciente", imagen_paciente);
            if (media_imagen_paciente != null) { params.put("media_imagen_paciente", media_imagen_paciente); }

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error en los datos", Toast.LENGTH_SHORT).show();
        }

        try {
            httpclient.post(servidor.urlServidorControlPaciente, params, new JsonHttpResponseHandler() {

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

                        int respuesta = response.getInt("Success");
                        if (respuesta == 1)
                        {
                            Toast.makeText(thisActivity, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.detach(PacienteRegistrar.this).attach(PacienteRegistrar.this).commit();
                        }
                        else if (respuesta == 0)
                        {
                            Snackbar.make(clPacienteRegistrarF, response.getString("Mensaje_Error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Snackbar.make(clPacienteRegistrarF, "Verifique la conexión con el servidor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            });

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }

    public String obtenerFecha()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public String obtenerHora()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        return simpleDateFormat.format(calendar.getTime());
    }

    public void dialogSeleccionarImagen()
    {
        final CharSequence[] options = { "Tomar foto", "Elegir de galeria", "Cancelar" };
        final AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(thisActivity);
        dialogbuilder.setTitle("Elige una opción");
        dialogbuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selection) {
                if (options[selection] == "Tomar foto") {
                    capturarImagen();
                } else if (options[selection] == "Elegir de galeria") {
                    SeleccionarImagenGaleria();
                } else if (options[selection] == "Cancelar") {
                    dialog.dismiss();
                }
            }
        });
        dialogbuilder.show();
    }

    public String generarNombreImagen()
    {
        if(!folder.exists()) {
            folder.mkdir();
            random_nombre = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            imagen_nombre = ("IMG_" + random_nombre);
        } else if (folder.exists()) {
            random_nombre = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            imagen_nombre = ("IMG_" + random_nombre);
        }
        return imagen_nombre;
    }

    public void capturarImagen()
    {
        imagen_directorio = folder + "/" + generarNombreImagen();
        file_imagen = new File(imagen_directorio + ".jpg");
        output = Uri.fromFile(file_imagen);
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intentCamara, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void SeleccionarImagenGaleria()
    {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria.createChooser(intentGaleria, "Selecciona la aplicación de imagenes"), CAMERA_SELECT_IMAGE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK)
                {
                    try
                    {
                        recortarImagen(output);
                    }
                    catch (Exception localException)
                    {
                        Toast.makeText(thisActivity, "Imagen cancelada", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(thisActivity, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAMERA_SELECT_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK)
                {
                    try
                    {
                        selectImage = data.getData();
                        recortarImagen(selectImage);
                    }
                    catch (Exception localException)
                    {
                        Toast.makeText(thisActivity, "Imagen cancelada", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(thisActivity, "Selección de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAMERA_CROP_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK)
                {
                    try
                    {
                        Bitmap localBitmap = data.getExtras().getParcelable("data");
                        ivImagenPacienteRegistrarF.setImageBitmap(localBitmap);
                        imagen_crop = (generarNombreImagen() + "_APP" + ".jpg");
                        imagen_directorio = folder + "/" + imagen_crop;
                        file_crop = new File(imagen_directorio);
                        FileOutputStream fileOutputStream = new FileOutputStream(file_crop);
                        localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                        if (output != null)
                        {
                            File localFile = new File(output.getPath());
                            if (localFile.exists()) { localFile.delete(); }
                        }
                    }
                    catch (Exception localException)
                    {
                        Toast.makeText(thisActivity, "Imagen cancelada", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(thisActivity, "Recorte de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void recortarImagen(Uri paramUri)
    {
        try
        {
            Intent intentRecortarImagen = new Intent("com.android.camera.action.CROP");
            intentRecortarImagen.setDataAndType(paramUri, "image/*");
            intentRecortarImagen.putExtra("crop", "true");
            intentRecortarImagen.putExtra("aspectX", 1);
            intentRecortarImagen.putExtra("aspectY", 1);
            intentRecortarImagen.putExtra("outputX", 300);
            intentRecortarImagen.putExtra("outputY", 300);
            intentRecortarImagen.putExtra("return-data", true);
            startActivityForResult(intentRecortarImagen, CAMERA_CROP_IMAGE_REQUEST_CODE);
        }
        catch (Exception localException)
        {
            Toast.makeText(thisActivity, "Error: No se pudo recortar imagen", Toast.LENGTH_SHORT).show();
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
