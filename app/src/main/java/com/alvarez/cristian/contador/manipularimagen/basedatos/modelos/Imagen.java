package com.alvarez.cristian.contador.manipularimagen.basedatos.modelos;

import android.content.Context;

import com.alvarez.cristian.contador.manipularimagen.basedatos.DBHelper;

/**
 * Created by CristianAlvarez on 21/10/2017.
 */

public class Imagen {
    private String ruta;
    private String estado;

    private DBHelper dbhelper;

    public Imagen(Context context, String ruta, String estado) {
        this.ruta = ruta;
        this.estado = estado;

        dbhelper = new DBHelper(context);
    }

    public Imagen() {
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void insertarImagen(){
        dbhelper.insertarImagen(getRuta(), getEstado());
    }

    public void actualizarImagen(){
        dbhelper.actualizarImagen(getRuta(), getEstado());
    }
}
