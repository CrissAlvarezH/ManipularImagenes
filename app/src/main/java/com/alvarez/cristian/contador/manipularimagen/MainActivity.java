package com.alvarez.cristian.contador.manipularimagen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.io.File;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Button btnTomarFoto, btnReescalarFoto, btnRotarFoto, btnGlide, btnEnviar;
    private ImageView imagen;
    private TextView txtDensidad, txtPesoImg;

    private MagicalCamera magicalCamera;
    private MagicalPermissions magicalPermissions;
    private final int REDIMENCIONAR_IMAGEN_PORCENTAGE = 40;

    private Bitmap imagenTomada = null;
    private String rutaImg, rutaReducida  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        magicalPermissions = new MagicalPermissions(this, permissions);

        magicalCamera = new MagicalCamera(this, REDIMENCIONAR_IMAGEN_PORCENTAGE, magicalPermissions);

        btnTomarFoto = (Button) findViewById(R.id.btn_tomar_foto);
        btnReescalarFoto = (Button) findViewById(R.id.btn_reescalar_foto);
        btnRotarFoto = (Button) findViewById(R.id.btn_rotar_img);
        btnGlide = (Button) findViewById(R.id.btn_glide);
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        txtDensidad = (TextView) findViewById(R.id.txt_density);
        txtPesoImg = (TextView) findViewById(R.id.txt_peso_img);
        imagen = (ImageView) findViewById(R.id.img_foto);
    }

    public void tomarFoto(View vista){
        magicalCamera.takePhoto();
    }

    public void redimencionarImagen(View v){
        if(imagenTomada != null) {
            Bitmap bitmapRedimencinado = redimencionar(this, imagenTomada, 300, 300);

            imagen.setImageBitmap(bitmapRedimencinado);

        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void rotarImagen(View v){
        if(imagenTomada != null){
            imagen.setImageBitmap(ponerHorizontal(imagenTomada));
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void bajarDensidadImagen(View v){
        if(imagenTomada != null){
            // le paso 512000 byte = 500 kilobyte
            //rutaReducida = ajustarImagenPesoDeseado(rutaImg, calcularSampleSize(pesoByteFile(rutaImg), 204800));

            /*String nuevaRuta = magicalCamera.savePhotoInMemoryDevice(
                    nuevoBitmap,// bitmap de la foto a guardar
                    "img",// nombre con el que se guardará la imagen
                    "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                    MagicalCamera.PNG,// formato de compresion
                    true // true: le agrega la fecha al nombre de la foto para no replicarlo
            );*/

            new Compressor(this)
                    .compressToFileAsFlowable(new File(rutaImg))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            txtPesoImg.setText("Peso KB: " + pesoKBytesFile(file.getAbsolutePath()));
                            imagen.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

                            rutaImg = file.getAbsolutePath();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });

            //imagen.setImageBitmap(imagenTomada);
            txtDensidad.setText("Density: " + imagenTomada.getDensity());
            //txtPesoImg.setText("Peso KB: " + pesoKBytesFile(nuevaRuta.getAbsolutePath()));
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void redimencionarConGlide(View v){
        if(imagenTomada != null){
            imagen.setImageBitmap(redimencionarGlide(new File(rutaImg), 300, 400));
            txtPesoImg.setText("Peso KB: " + pesoKBytesFile(rutaImg));
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void enviarFoto(View v){
        if(imagenTomada != null) {
            // aqui iba rutaReducida != null
            if (true){
                EnviarImagen enviarImagen = new EnviarImagen(rutaImg);
                Thread hilo = new Thread(enviarImagen);
                hilo.start();
            }else{
                Toast.makeText(this, "Redusca la imagen.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        magicalCamera.resultPhoto(requestCode, resultCode, data);

        /*Guarda la foto en la memoria interna del dispositivo, si no tiene espacio, pasa a
         * guardarla en la SD card, retorna la ruta en la cual almacenó la foto */
        rutaImg = magicalCamera.savePhotoInMemoryDevice(
                magicalCamera.getPhoto(),// bitmap de la foto a guardar
                "img",// nombre con el que se guardará la imagen
                "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                MagicalCamera.PNG,// formato de compresion
                true // true: le agrega la fecha al nombre de la foto para no replicarlo
        );

        imagenTomada = magicalCamera.getPhoto();

//        txtDensidad.setText(txtDensidad.getText().toString() + magicalCamera.getPhoto().getDensity());
        txtPesoImg.setText("Peso KB: " + pesoKBytesFile(rutaImg));

        imagen.setImageBitmap(imagenTomada);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        magicalPermissions.permissionResult(requestCode, permissions, grantResults);
    }

    private Bitmap redimencionar(Context contexto, Bitmap bitmapOriginal, int nuevoAncho, int nuevoAlto){
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

    private Bitmap ponerHorizontal(Bitmap bitmapOriginal){
        if(bitmapOriginal.getWidth() < bitmapOriginal.getHeight()){
            // rotamos 90 grados la imagen pasada por parametros
            return magicalCamera.rotatePicture(bitmapOriginal, MagicalCamera.ORIENTATION_ROTATE_90);
        }

        return bitmapOriginal;
    }

    private String modificarAncho(String ruta, int anchoDeseado){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(ruta, options);
        int anchoOriginal = options.outWidth;

        options.inScaled = true;
        options.inDensity = anchoOriginal;
        options.inTargetDensity = ((int) (anchoOriginal * 0.5));

        Bitmap bitmapReducido = BitmapFactory.decodeFile(ruta, options);

        String nuevaRuta = magicalCamera.savePhotoInMemoryDevice(
                bitmapReducido,// bitmap de la foto a guardar
                "img",// nombre con el que se guardará la imagen
                "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                MagicalCamera.PNG,// formato de compresion
                true // true: le agrega la fecha al nombre de la foto para no replicarlo
        );

        //Log.e("nuevaRuta", nuevaRuta);

        return nuevaRuta;
    }

    public String reducirImagen(String rutaImg, long tamanoMaximo){
        File img = new File(rutaImg);
        String nuevaRuta = null;

        BitmapFactory.Options opciones = new BitmapFactory.Options();
        Bitmap bitmap = null;
        opciones.inSampleSize = 1;// esto da un 1/n el tamaño original de la imagen

        while(img.length() > tamanoMaximo){
            img = new File(rutaImg);
            opciones.inSampleSize = opciones.inSampleSize + 1;

            // creamos un bitmap en la ruta y con el tamaño en las opciones especificadas
            bitmap = BitmapFactory.decodeFile(rutaImg, opciones);

            // TODO poner metodo para eliminar la imagen antes de ser reducida

            // TODO crear metodo para guardar imagen saveImage(bitmap, ruta)

            nuevaRuta = magicalCamera.savePhotoInMemoryDevice(
                    bitmap,// bitmap de la foto a guardar
                    "img",// nombre con el que se guardará la imagen
                    "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                    MagicalCamera.PNG,// formato de compresion
                    true // true: le agrega la fecha al nombre de la foto para no replicarlo
            );

            img = new File(nuevaRuta);
        }

        return nuevaRuta;
    }

    private String ajustarImagenPesoDeseado(String ruta, int sampleSize){
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        Bitmap bitmap = null;
        opciones.inSampleSize = sampleSize;// esto da un 1/n el tamaño original de la imagen

        bitmap = BitmapFactory.decodeFile(ruta, opciones);

        String nuevaRuta = magicalCamera.savePhotoInMemoryDevice(
                bitmap,// bitmap de la foto a guardar
                "img",// nombre con el que se guardará la imagen
                "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                MagicalCamera.PNG,// formato de compresion
                true // true: le agrega la fecha al nombre de la foto para no replicarlo
        );

        return nuevaRuta;

    }

    private long pesoKBytesFile(String rutaFile){
        Log.e("rutaFile", rutaFile+" ---");
        File file = new File(rutaFile+"");

        return (file.length() / 1024);
    }

    private long pesoByteFile(String rutaFile){
        return new File(rutaFile).length();
    }

    private Bitmap redimencionarGlide(File rutaBipmap, int ancho, int alto){
        try {
            Bitmap bitmap = Glide.with(this)
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

    private int calcularSampleSize(long tamOriginal, long tamDeseado){
        long porcentajeDeseado = ((tamDeseado * 100) / tamOriginal);

        // empieza desde el 1/2
        double [] porcentajes = {50, 33.3, 25, 20, 16.6, 14.3, 12.5, 11.1, 10, 9, 8.3, 7.7, 7.1, 6.7, 6.2};
        int [] enes = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int n = 1;


        for(int i=0; i<porcentajes.length; i++){
            if(porcentajeDeseado >= (porcentajes[i] * 0.33)){
                n = enes[i];
                break;
            }
        }

        Log.e("tamO, tamD, porceD, n ", tamOriginal+", "+tamDeseado+", "+porcentajeDeseado+", "+n);

        return n;
    }
}
