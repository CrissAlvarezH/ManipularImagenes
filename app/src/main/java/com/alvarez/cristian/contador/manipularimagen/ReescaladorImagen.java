package com.alvarez.cristian.contador.manipularimagen;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.common.data.BitmapTeleporter;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CristianAlvarez on 14/10/2017.
 */

public class ReescaladorImagen {
    private static File rutaNuevaImg;

    public static Bitmap cambiarAncho(Bitmap bitmap, int nuevoAncho){
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        float porcentajeDeAncho = (float) nuevoAncho/ancho;

        Log.e("porcentaje", porcentajeDeAncho+"");

        int anchoEscalado =  nuevoAncho;
        int altoEscalado = (int) (alto * porcentajeDeAncho);

        Log.e("anchoOriginal", ancho+"");
        Log.e("anchoEscalado", anchoEscalado+"");

        Bitmap nuevoBitmap = Bitmap.createScaledBitmap(bitmap, anchoEscalado, altoEscalado, true);

        return nuevoBitmap;
    }

    public static Bitmap comprimirConCompressorToBitmap(String rutaImage, Context contexto){
        Bitmap imagen = null;
        Log.v("Compressor", "iantes de try");

        try {
            imagen = new Compressor(contexto)
                    .setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(new File(rutaImage));

            Log.v("Compressor", "imagen comprimida");

        } catch (Exception e) {
            Log.v("ErrorComprimir", e.getMessage());
            e.printStackTrace();
        }

        return imagen;
    }

    public static File coprimirConCompressorToFile(String rutaImage, Context context){


        new Compressor(context)
                .compressToFileAsFlowable(new File(rutaImage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        rutaNuevaImg = file;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        rutaNuevaImg = null;
                    }
                });

        return rutaNuevaImg;
    }
}
