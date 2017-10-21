package com.alvarez.cristian.contador.manipularimagen.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CristianAlvarez on 21/10/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String NOMBRE_BASE_DATOS = "basededatos.sqlite";
    private final static int VERSION_ESQUEMA = 1;

    public DBHelper(Context context) {
        super(context, NOMBRE_BASE_DATOS, null, VERSION_ESQUEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREAR_TABLA_IMAGENES = "CREATE TABLE imagenes (" +
                "direccion TEXT PRIMARY KEY," +// Ruta de la imagen (tiene que se unica)
                "estado TEXT);";// enviada, no_enviada

        database.execSQL(CREAR_TABLA_IMAGENES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
