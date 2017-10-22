package com.alvarez.cristian.contador.manipularimagen.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alvarez.cristian.contador.manipularimagen.basedatos.DBHelper;
import com.alvarez.cristian.contador.manipularimagen.utilidades.Constantes;

import java.util.Timer;
import java.util.TimerTask;

public class ServicieIntentarEnviar extends Service {
    private TimerTaskIniciarCiclicamente tarea;

    public ServicieIntentarEnviar() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("onCreate", "ServicioIntentarEnviarImg");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("onStarCommand", "ServicioIntentarEnviarImg");

        tarea = new TimerTaskIniciarCiclicamente(getApplicationContext());
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(tarea, 1, 1 * 60 * 1000);


        return START_STICKY;
    }

    private class TimerTaskIniciarCiclicamente extends TimerTask {
        private DBHelper dbHelper;

        public TimerTaskIniciarCiclicamente(Context context){
            dbHelper = new DBHelper(context);
        }

        @Override
        public void run() {
            // si hay mas de una imagen por enviar
            if(dbHelper.rutaImagenesNoEnviadas().size() > 0){
                Log.v("Imagenes", "Hay "+dbHelper.rutaImagenesNoEnviadas().size()+" imagenes por enviar.");
                if(!ManagerServicioEnviarImagenes.estaEncendido(getApplicationContext())){
                    Intent i = new Intent(getApplicationContext(), ProgresoEnvioImagenesService.class);
                    // establecemos una accion en el intent de correr el servicio de enviar imagenes
                    i.setAction(Constantes.ACCION_CORRER_SERVICIO_ENVIAR_IMAGEN);

                    // iniciamos el servicio, el se destruye una vez termina la pila de intens
                    startService(i);
                }else{
                    Log.v("IntentService", "Esta encendido");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("onDestroy", "ServicioIntentarEnviarImg");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
