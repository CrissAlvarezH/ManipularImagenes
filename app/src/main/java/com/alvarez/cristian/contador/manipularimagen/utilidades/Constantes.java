package com.alvarez.cristian.contador.manipularimagen.utilidades;

import com.alvarez.cristian.contador.manipularimagen.services.*;

/**
 * Created by CristianAlvarez on 22/10/2017.
 */

public class Constantes {
    /**
     * Constantes para {@link ProgresoEnvioImagenesService}
     * Corren y paran el servicio respectivamente
     */
    public static final String ACCION_CORRER_SERVICIO_ENVIAR_IMAGEN = "com.alvarez.cristian.contador.manipularimagen.services.action.RUN_SERVICE_IMAGES";
    public static final String ACCION_PROGESO_SALIR = "com.alvarez.cristian.contador.manipularimagen.services.action.PROGRESS_EXIT";

    // extra del progreso que va (imagenes enviadas)
    public static final String EXTRA_PROGRESO = "com.alvarez.cristian.contador.manipularimagen.services.extra.PROGRESS";
    // numero de imagenes por enviar
    public static final String EXTRA_IMAGENES_POR_ENVIAR = "com.alvarez.cristian.contador.manipularimagen.services.extra.TOTAL_IMAGES";
}
