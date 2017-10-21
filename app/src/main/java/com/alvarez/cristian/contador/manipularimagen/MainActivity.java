package com.alvarez.cristian.contador.manipularimagen;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.io.File;

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
            Bitmap bitmapRedimencinado = ManipuladorImagen.redimencionar(this, imagenTomada, 300, 300);

            imagen.setImageBitmap(bitmapRedimencinado);

        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void rotarImagen(View v){
        if(imagenTomada != null){
            imagen.setImageBitmap(ManipuladorImagen.ponerHorizontal(imagenTomada, magicalCamera));
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
                            txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(file.getAbsolutePath()));
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
            imagen.setImageBitmap(ManipuladorImagen.redimencionarGlide(this, new File(rutaImg), 300, 400));
            txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(rutaImg));
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
        txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(rutaImg));

        imagen.setImageBitmap(imagenTomada);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        magicalPermissions.permissionResult(requestCode, permissions, grantResults);
    }

    private long pesoByteFile(String rutaFile){
        return new File(rutaFile).length();
    }
}
