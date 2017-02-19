package com.brandon.chavez.android.app.sngapp.utilidades;

public class Servidor {

    private static final String ipServer = "192.168.137.99";
    public final String urlServidorControlUsuario = "http://"+ipServer+"/SNGApp/control/control_usuario.php";
    public final String urlServidorControlPaciente = "http://"+ipServer+"/SNGApp/control/control_paciente.php";
    public final String urlServidorControlPacienteImagen = "http://"+ipServer+"/SNGApp/utilities/images/pacientes/";
    public final String urlServidorControlHistorial = "http://"+ipServer+"/SNGApp/control/control_historial.php";

}
