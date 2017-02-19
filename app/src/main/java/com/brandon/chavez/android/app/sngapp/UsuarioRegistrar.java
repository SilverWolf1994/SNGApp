package com.brandon.chavez.android.app.sngapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class UsuarioRegistrar extends AppCompatActivity {

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
    private Servidor servidor;
    private ProgressDialog progressDialog;
    private CoordinatorLayout clUsuarioRegistrarA;
    private ImageView ivImagenUsuarioRegistrarA;
    private Spinner spTipoUsuarioRegistrarA;
    private EditText etCodigoUsuarioRegistrarA, etNombreUsuarioRegistrarA,
            etApellidoUsuarioRegistrarA, etEmailUsuarioRegistrarA, etPasswordUsuarioRegistrarA,
            etConfirmarPasswordUsuarioRegistrarA;
    private RadioButton rbFemeninoUsuarioRegistrarA, rbMasculinoUsuarioRegistrarA;
    private FloatingActionButton fabImagenUsuarioRegistrarA, fabGuardarUsuarioRegistrarA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_registrar);
        thisActivity = this;
        servidor = new Servidor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clUsuarioRegistrarA = (CoordinatorLayout) findViewById(R.id.clUsuarioRegistrar);
        ivImagenUsuarioRegistrarA = (ImageView) findViewById(R.id.ivImagenUsuarioRegistrar);
        spTipoUsuarioRegistrarA = (Spinner) findViewById(R.id.spTipoUsuarioRegistrar);
        etCodigoUsuarioRegistrarA = (EditText) findViewById(R.id.etCodigoUsuarioRegistrar);
        etNombreUsuarioRegistrarA = (EditText) findViewById(R.id.etNombreUsuarioRegistrar);
        etApellidoUsuarioRegistrarA = (EditText) findViewById(R.id.etApellidoUsuarioRegistrar);
        etEmailUsuarioRegistrarA = (EditText) findViewById(R.id.etEmailUsuarioRegistrar);
        etPasswordUsuarioRegistrarA = (EditText) findViewById(R.id.etPasswordUsuarioRegistrar);
        etConfirmarPasswordUsuarioRegistrarA = (EditText) findViewById(R.id.etConfirmarPasswordUsuarioRegistrar);
        rbFemeninoUsuarioRegistrarA = (RadioButton) findViewById(R.id.rbFemeninoUsuarioRegistrar);
        rbMasculinoUsuarioRegistrarA = (RadioButton) findViewById(R.id.rbMasculinoUsuarioRegistrar);
        fabImagenUsuarioRegistrarA = (FloatingActionButton) findViewById(R.id.fabImagenUsuarioRegistrar);
        fabGuardarUsuarioRegistrarA = (FloatingActionButton) findViewById(R.id.fabGuardarUsuarioRegistrar);

        fabImagenUsuarioRegistrarA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSeleccionarImagen();
            }
        });

        fabGuardarUsuarioRegistrarA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipo = spTipoUsuarioRegistrarA.getSelectedItem().toString();
                String codigo = etCodigoUsuarioRegistrarA.getText().toString();
                String nombre = etNombreUsuarioRegistrarA.getText().toString();
                String apellido = etApellidoUsuarioRegistrarA.getText().toString();
                String email = etEmailUsuarioRegistrarA.getText().toString();
                String password = etPasswordUsuarioRegistrarA.getText().toString();
                String confirmarPassword = etConfirmarPasswordUsuarioRegistrarA.getText().toString();

                if (!comprobarCampos(codigo, nombre, apellido, email, password, confirmarPassword)) {
                    if (confirmarPasswordUsuario(password, confirmarPassword)) {
                        registrarUsuarioBD(tipo, codigo, nombre, apellido, email, seleccionarGenero(), password,
                                obtenerFecha(), obtenerHora(), imagen_crop, file_crop);
                    } else {
                        Snackbar.make(clUsuarioRegistrarA, "[!] Campos de password no iguales", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(clUsuarioRegistrarA, "[!] Comprobar campos vacios", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    public void registrarUsuarioBD(String tipo_usuario, String codigo_usuario, String nombre_usuario, String apellido_usuario,
                                   String email_usuario, String genero_usuario, String password_usuario,
                                   String fecha_registro_usuario, String hora_registro_usuario, String imagen_usuario,
                                   File media_imagen_usuario)
    {
        AsyncHttpClient httpclient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        try {
            params.put("opcion", 1);
            params.put("tipo_usuario", tipo_usuario);
            params.put("codigo_usuario", codigo_usuario);
            params.put("nombre_usuario", nombre_usuario);
            params.put("apellido_usuario", apellido_usuario);
            params.put("email_usuario", email_usuario);
            params.put("genero_usuario", genero_usuario);
            params.put("password_usuario", password_usuario);
            params.put("fecha_registro_usuario", fecha_registro_usuario);
            params.put("hora_registro_usuario", hora_registro_usuario);
            params.put("imagen_usuario", imagen_usuario);
            if (media_imagen_usuario != null) { params.put("media_imagen_usuario", media_imagen_usuario); }

        } catch (Exception exception) {
            Toast.makeText(thisActivity, "Error en los datos", Toast.LENGTH_SHORT).show();
        }

        try {
            httpclient.post(servidor.urlServidorControlUsuario, params, new JsonHttpResponseHandler() {

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
                            String codigoUsuarioBD = response.getString("Codigo_Usuario");
                            String nombreUsuarioBD = response.getString("Nombre_Usuario");
                            String apellidoUsuarioBD = response.getString("Apellido_Usuario");

                            Intent intentusuariologin = new Intent(thisActivity, UsuarioLogin.class);
                            Toast.makeText(thisActivity, "Registro exitoso! \n" + nombreUsuarioBD + " " + apellidoUsuarioBD + " con codigo: " + codigoUsuarioBD, Toast.LENGTH_LONG).show();
                            startActivity(intentusuariologin);
                            onFinish();

                        } else if (respuesta == 0)
                        {
                            Snackbar.make(clUsuarioRegistrarA, response.getString("Mensaje_Error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }catch (JSONException jsonexception) {
                        Toast.makeText(thisActivity, "Error al recibir datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    progressDialog.dismiss();
                    Snackbar.make(clUsuarioRegistrarA, "Verifique la conexión con el servidor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    public String seleccionarGenero()
    {
        String genero = "";
        if (rbFemeninoUsuarioRegistrarA.isChecked()) {
            genero = rbFemeninoUsuarioRegistrarA.getText().toString();
        } else if (rbMasculinoUsuarioRegistrarA.isChecked()) {
            genero = rbMasculinoUsuarioRegistrarA.getText().toString();
        }
        return genero;
    }

    public boolean comprobarCampos(String codigo, String nombre, String apellido, String email, String password, String confirmarPassword)
    {
        return codigo.equals("") || nombre.equals("") || apellido.equals("") || email.equals("") || password.equals("") || confirmarPassword.equals("");
    }

    public boolean confirmarPasswordUsuario(String password, String confirmar_password)
    {
        return password.equals(confirmar_password);
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
                        ivImagenUsuarioRegistrarA.setImageBitmap(localBitmap);
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

}
