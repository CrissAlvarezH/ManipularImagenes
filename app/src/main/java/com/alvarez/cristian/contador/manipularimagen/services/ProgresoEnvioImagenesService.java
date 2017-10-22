package com.alvarez.cristian.contador.manipularimagen.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alvarez.cristian.contador.manipularimagen.EnviarImagen;
import com.alvarez.cristian.contador.manipularimagen.basedatos.DBHelper;
import com.alvarez.cristian.contador.manipularimagen.basedatos.modelos.Imagen;
import com.alvarez.cristian.contador.manipularimagen.utilidades.Constantes;

import java.util.ArrayList;

public class ProgresoEnvioImagenesService extends IntentService {
    private DBHelper dbHelper;

    public ProgresoEnvioImagenesService() {
        super("ProgresoEnvioImagenesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            // si la accion es correr el servicio de enviar las imagenes
            if(Constantes.ACCION_CORRER_SERVICIO_ENVIAR_IMAGEN.equals(action)){
                enviarImagenes();
            }
        }
    }

    private void enviarImagenes(){
        // instanciamos el helper de la base de datos
        dbHelper = new DBHelper(this);

        // Se construye la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Enviando imagenes")
                .setContentText("Procesado...");

        int contadorImgEnviada = 0, contadorImgNoEnviada = 0;
        ArrayList<String> imagenesPorEnviar = dbHelper.rutaImagenesNoEnviadas();
        int cantidadImagenesPorEnvira = imagenesPorEnviar.size();

        for(String rutaImagen : imagenesPorEnviar){
            Imagen imagen = new Imagen(this, rutaImagen, "no_enviada");

            // tratamos en eviar la imagen y verificamos que se envio y las contamos
            if(EnviarImagen.subirArchivo(imagen)){
                contadorImgEnviada++;
            }else {
                contadorImgNoEnviada++;
            }

            // actualizamos el progreso de la notificacion
            builder.setProgress(cantidadImagenesPorEnvira, contadorImgEnviada, false);
            builder.setContentText("Enviando... ("+contadorImgEnviada+"/"+imagenesPorEnviar+")");
            // ponemos en primer plano la notificacion y le asignamos un id
            startForeground(123, builder.build());

            Log.v("EnvioDeImagenes", "Progreso: ("+contadorImgEnviada+" de "+imagenesPorEnviar+
            ") imagenes no enviadas: ("+contadorImgNoEnviada+" de "+imagenesPorEnviar+")");
        }

        // quitar del primer plano el proceso
        stopForeground(true);// true: removemos la notificacion
    }

    @Override
    public void onDestroy() {
        Log.v("onDestroy", "IntentServiceEnviarImagenes destruido");
    }
}
