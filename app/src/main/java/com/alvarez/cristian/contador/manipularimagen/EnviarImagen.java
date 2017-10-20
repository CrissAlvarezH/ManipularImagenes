package com.alvarez.cristian.contador.manipularimagen;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by CristianAlvarez on 23/09/2017.
 */

public class EnviarImagen implements Runnable {
    private String ruta;

    public EnviarImagen(String ruta){
        this.ruta = ruta;
    }

    @Override
    public void run() {
        subirArchivoVideo(ruta);
    }

    public void subirArchivoVideo(String ruta){// metodo
        Log.v("ruta_archivo", ruta);
        File file = new File(ruta);
        String tipo_de_contenido = getTipo(file.getPath());// si es img, mp3, video, etc

        String ruta_archivo = file.getAbsolutePath();
        OkHttpClient client = new OkHttpClient();
        RequestBody file_body = RequestBody.create(MediaType.parse(tipo_de_contenido), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", tipo_de_contenido)
                .addFormDataPart("uploaded_file", ruta_archivo.substring(ruta_archivo.lastIndexOf("/")+1), file_body)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.100.62/subir_archivo/subir_video.php")
                .post(requestBody)
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();

            Log.e("respuesta ", response.body().string());

            if(!response.isSuccessful()){
                Log.v("SubirArchivoVideo", "no es exitoso");
            }else{
                Log.v("SubirArchivoVideo", "es exitoso");
            }
        } catch (IOException e) {
            Log.v("ErrorAlEjecutar", e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTipo(String path){// metodo 1.1
        String extencion = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extencion);
    }
}
