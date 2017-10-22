package com.alvarez.cristian.contador.manipularimagen.utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.android.gms.common.data.BitmapTeleporter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CristianAlvarez on 14/10/2017.
 */

public class ManipuladorImagen {
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

    public static Bitmap redimencionarGlide(Context contexto, File rutaBipmap, int ancho, int alto){
        try {
            Bitmap bitmap = Glide.with(contexto)
                    .load(rutaBipmap)
                    .asBitmap()// obtenemos el bitmap
                    // pasamos a la mitad del ancho y alto
                    .override(ancho, alto)
                    .fitCenter() // fijamos en el centro
                    .into(-1, -1)// nos da un bitmap con el tamaño orginal hasta este punto
                    .get();// obtenemos el bitmap

            return bitmap;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap ponerHorizontal(Bitmap bitmapOriginal, MagicalCamera magicalCamera){
        if(bitmapOriginal.getWidth() < bitmapOriginal.getHeight()){
            // rotamos 90 grados la imagen pasada por parametros
            return magicalCamera.rotatePicture(bitmapOriginal, MagicalCamera.ORIENTATION_ROTATE_90);
        }

        return bitmapOriginal;
    }

    public static long pesoKBytesFile(String rutaFile){
        Log.e("rutaFile", rutaFile+" ---");
        File file = new File(rutaFile+"");

        return (file.length() / 1024);
    }

    public static Bitmap redimencionar(Context contexto, Bitmap bitmapOriginal, int nuevoAncho, int nuevoAlto){
        int anchoOriginal = bitmapOriginal.getWidth();
        int altoOriginal = bitmapOriginal.getHeight();

        // calculamos el escalado de la imagen destino
        float anchoEscalado = ((float) nuevoAncho) / anchoOriginal;
        float altoEscalado = ((float) nuevoAlto) / altoOriginal;

        // Creamos una matrix para manipular la imagen
        Matrix matrix = new Matrix();
        // Redimencionamos el bitmap
        matrix.postScale(anchoEscalado, altoEscalado);

        // Volvemos a crear la imagen con los nuevos valores
        Bitmap bitmapEscalado = Bitmap.createBitmap(bitmapOriginal, 0, 0, anchoOriginal, altoOriginal,
                matrix, true);

        return bitmapEscalado;
    }
}
