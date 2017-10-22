package com.alvarez.cristian.contador.manipularimagen.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alvarez.cristian.contador.manipularimagen.utilidades.Constantes;

/**
 * Created by CristianAlvarez on 22/10/2017.
 */

public class ManagerServicioEnviarImagenes {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void iniciarServicio(Context contexto){
        Log.v("iniciarServicio, on", estaEncendido(contexto)+"");
        if(!estaEncendido(contexto)) {
            Intent i = new Intent(contexto, ProgresoEnvioImagenesService.class);
            // establecemos una accion en el intent de correr el servicio de enviar imagenes
            i.setAction(Constantes.ACCION_CORRER_SERVICIO_ENVIAR_IMAGEN);

            // iniciamos el servicio, el se destruye una vez termina la pila de intens
            contexto.startService(i);
            editarPreferences(contexto, "on");
        }
    }

    public static void pararServicio(Context contexto){
        Log.v("pararServicio, on", estaEncendido(contexto)+"");
        if(estaEncendido(contexto)){
            editarPreferences(contexto, "off");
        }
    }

    public static void permutarServicio(Context contexto){
        Log.v("servicioIniciado", estaEncendido(contexto)+"");
        if(estaEncendido(contexto))
            ManagerServicioEnviarImagenes.pararServicio(contexto);
        else
            ManagerServicioEnviarImagenes.iniciarServicio(contexto);
    }

    private static void editarPreferences(Context contexto, String onOff){
        // editamos las preferencias, metemos una variable llamada encendido que es una vandera para indicar
        // si el servicio esta o no encendido
        inicializarPreferencias(contexto);
        editor = preferences.edit();

        editor.putString("encendido", onOff);
        editor.commit();
    }

    public static boolean estaEncendido(Context contexto){
        inicializarPreferencias(contexto);

        if(preferences.getString("encendido", "primera_vez").equals("primera_vez")){
            // si es la primera vez, si no existe la variable "encendido" lo ponemos como apagado
            editor = preferences.edit();
            editor.putString("encendido", "off");
            editor.commit();
            return false;
        }else{
            return preferences.getString("encendido", "").equals("on");
        }

    }

    private static void inicializarPreferencias(Context context){
        // creamos unas preferencias con el nombre servicio o las obtenemos si ya existen
        preferences = context.getSharedPreferences("servicio_enviar_img", Context.MODE_PRIVATE);
    }
    
}
