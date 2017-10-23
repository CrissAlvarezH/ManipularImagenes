package com.alvarez.cristian.contador.manipularimagen.utilidades;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CristianAlvarez on 23/10/2017.
 */

public class RedUtils {
    public static boolean estaNetDisponible(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // si el objeto no es nulo y si se esta conectado a alguna red de internet
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isOnlineNet() {
        try {
            // mandamos un ping a google para verificar coneccion a internet funcional
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();// retorna 0 por lo general cuando el proceso finalizo correctamente

            return (val == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
