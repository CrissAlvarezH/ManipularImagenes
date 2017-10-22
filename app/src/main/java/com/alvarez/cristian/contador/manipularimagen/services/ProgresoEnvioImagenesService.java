package com.alvarez.cristian.contador.manipularimagen.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class ProgresoEnvioImagenesService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.alvarez.cristian.contador.manipularimagen.services.action.FOO";
    private static final String ACTION_BAZ = "com.alvarez.cristian.contador.manipularimagen.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.alvarez.cristian.contador.manipularimagen.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.alvarez.cristian.contador.manipularimagen.services.extra.PARAM2";

    public ProgresoEnvioImagenesService() {
        super("ProgresoEnvioImagenesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }

}
