package com.brandon.chavez.android.app.sngapp.utilidades;

import com.brandon.chavez.android.app.sngapp.UsuarioLogin;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.Intent;
import java.util.HashMap;
import java.util.Objects;

public class DatosSharedPreferences {

    private SharedPreferences sharedPreferences;
    private Editor spEditor;
    private Context spContext;
    private static final String NOMBRE_SHARED_PREFERENCE = "ServerSharedPreferences";

    private static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String CODIGO_USUARIO = "CODIGO_USUARIO";
    public static final String NOMBRE_USUARIO = "NOMBRE_USUARIO";
    public static final String EMAIL_USUARIO = "EMAIL_USUARIO";
    public static final String TIPO_USUARIO = "TIPO_USUARIO";

    public DatosSharedPreferences(Context context) {
        this.spContext = context;
        int spPrivateMode = 0;
        sharedPreferences = spContext.getSharedPreferences(NOMBRE_SHARED_PREFERENCE, spPrivateMode);
        spEditor = sharedPreferences.edit();
        spEditor.apply();
    }

    public void createLoginSession(String codigo, String nombre, String email, String tipo) {
        spEditor.putBoolean(LOGIN_STATUS, true);
        spEditor.putString(CODIGO_USUARIO, codigo);
        spEditor.putString(NOMBRE_USUARIO, nombre);
        spEditor.putString(EMAIL_USUARIO, email);
        spEditor.putString(TIPO_USUARIO, tipo);
        spEditor.commit();
    }

    public void checkLogin() {
        if(!isLoggedIn()){
            Intent intentlogin = new Intent(spContext, UsuarioLogin.class);
            intentlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentlogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            spContext.startActivity(intentlogin);
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> datosUsuario = new HashMap<>();
        datosUsuario.put(CODIGO_USUARIO, sharedPreferences.getString(CODIGO_USUARIO, null));
        datosUsuario.put(NOMBRE_USUARIO, sharedPreferences.getString(NOMBRE_USUARIO, null));
        datosUsuario.put(EMAIL_USUARIO, sharedPreferences.getString(EMAIL_USUARIO, null));
        datosUsuario.put(TIPO_USUARIO, sharedPreferences.getString(TIPO_USUARIO, null));
        return datosUsuario;
    }

    public void logoutUser() {
        spEditor.clear();
        spEditor.commit();
        Intent intentlogin = new Intent(spContext, UsuarioLogin.class);
        intentlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentlogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        spContext.startActivity(intentlogin);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(LOGIN_STATUS, false);
    }
}
