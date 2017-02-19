package com.brandon.chavez.android.app.sngapp.utilidades;

public class DatosPacientesListado
{
    private String codigoPaciente, nombrePaciente, apellidoPaciente, imagenPaciente;

    public void setCodigoPaciente(String codigoPaciente) { this.codigoPaciente = codigoPaciente; }

    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public void setApellidoPaciente(String apellidoPaciente) { this.apellidoPaciente = apellidoPaciente; }

    public void setImagenPaciente(String imagenPaciente) { this.imagenPaciente = imagenPaciente; }

    public String getCodigoPaciente()
    {
        return codigoPaciente;
    }

    public String getNombrePaciente()
    {
        return nombrePaciente;
    }

    public String getApellidoPaciente()
    {
        return apellidoPaciente;
    }

    public String getImagenPaciente()
    {
        return imagenPaciente;
    }
}
