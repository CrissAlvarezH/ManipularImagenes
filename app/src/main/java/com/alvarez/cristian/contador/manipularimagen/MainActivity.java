package com.alvarez.cristian.contador.manipularimagen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarez.cristian.contador.manipularimagen.basedatos.DBHelper;
import com.alvarez.cristian.contador.manipularimagen.basedatos.modelos.Imagen;
import com.alvarez.cristian.contador.manipularimagen.services.ManagerServicioEnviarImagenes;
import com.alvarez.cristian.contador.manipularimagen.services.ProgresoEnvioImagenesService;
import com.alvarez.cristian.contador.manipularimagen.services.ServicieIntentarEnviar;
import com.alvarez.cristian.contador.manipularimagen.utilidades.Constantes;
import com.alvarez.cristian.contador.manipularimagen.utilidades.ManipuladorImagen;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import java.io.File;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Button btnTomarFoto, btnReescalarFoto, btnRotarFoto, btnGlide, btnEnviar;
    private ImageView imgImagen;
    private TextView txtDensidad, txtPesoImg;
    private LinearLayout layoutProgreso;

    private MagicalCamera magicalCamera;
    private MagicalPermissions magicalPermissions;
    private final int REDIMENCIONAR_IMAGEN_PORCENTAGE = 40;

    private Bitmap imagenTomada = null;
    private String rutaImg, rutaReducida  = null;

    private DBHelper dbhelper;
    private SQLiteDatabase database;


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
        imgImagen = (ImageView) findViewById(R.id.img_foto);
        layoutProgreso = (LinearLayout) findViewById(R.id.layout_progreso);

        dbhelper = new DBHelper(this);
        database = dbhelper.getWritableDatabase();// creamos o abrimos la base de datos al iniciar esta activity

        // iniciamos el servicio que intenta enviar las imagenes cada x tiempo
        startService(new Intent(this, ServicieIntentarEnviar.class));
    }

    public void tomarFoto(View vista){
        magicalCamera.takePhoto();
    }

    public void redimencionarImagen(View v){
        if(imagenTomada != null) {
            Bitmap bitmapRedimencinado = ManipuladorImagen.redimencionar(this, imagenTomada, 300, 300);

            imgImagen.setImageBitmap(bitmapRedimencinado);

        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void rotarImagen(View v){
        if(imagenTomada != null){
            imgImagen.setImageBitmap(ManipuladorImagen.ponerHorizontal(imagenTomada, magicalCamera));
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
                    "img",// nombre con el que se guardará la imgImagen
                    "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                    MagicalCamera.PNG,// formato de compresion
                    true // true: le agrega la fecha al nombre de la foto para no replicarlo
            );*/

            new Compressor(this)
                    .setMaxWidth(400)
                    .setMaxHeight(300)
                    .compressToFileAsFlowable(new File(rutaImg))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(file.getAbsolutePath()));
                            imgImagen.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

                            rutaImg = file.getAbsolutePath();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });

            //imgImagen.setImageBitmap(imagenTomada);
            txtDensidad.setText("Density: " + imagenTomada.getDensity());
            //txtPesoImg.setText("Peso KB: " + pesoKBytesFile(nuevaRuta.getAbsolutePath()));
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void redimencionarConGlide(View v){
        if(imagenTomada != null){
            imgImagen.setImageBitmap(ManipuladorImagen.redimencionarGlide(this, new File(rutaImg), 300, 400));
            txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(rutaImg));
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void enviarFoto(View v){
        // aqui iba imagenTomada != null
        if(true) {
            // aqui iba rutaReducida != null
            if (true){
                Intent i = new Intent(this, ProgresoEnvioImagenesService.class);
                // establecemos una accion en el intent de correr el servicio de enviar imagenes
                i.setAction(Constantes.ACCION_CORRER_SERVICIO_ENVIAR_IMAGEN);

                // iniciamos el servicio, el se destruye una vez termina la pila de intens
                startService(i);
            }else{
                Toast.makeText(this, "Redusca la imgImagen.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarImagen(View v){
        if(imagenTomada != null) {
            Imagen imagen = new Imagen(this, rutaImg, "no_enviada");
            imagen.insertarImagen();// insertamos en la base de datos

            Toast.makeText(this, "imagen guardada", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Tome la foto primero.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        magicalCamera.resultPhoto(requestCode, resultCode, data);

        layoutProgreso.setVisibility(View.VISIBLE);// Hacemos visible el progreso

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ManipuladorImagen.ponerHorizontal(magicalCamera.getPhoto(), magicalCamera);

                /*Guarda la foto en la memoria interna del dispositivo, si no tiene espacio, pasa a
                * guardarla en la SD card, retorna la ruta en la cual almacenó la foto */
                rutaImg = magicalCamera.savePhotoInMemoryDevice(
                        bitmap,// bitmap de la foto a guardar
                        "img",// nombre con el que se guardará la imgImagen
                        "prueba_imagenes",// nombre de la carpeta donde se guardarán las fotos
                        MagicalCamera.PNG,// formato de compresion
                        true // true: le agrega la fecha al nombre de la foto para no replicarlo
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtPesoImg.setText("Peso KB: " + ManipuladorImagen.pesoKBytesFile(rutaImg));
                        layoutProgreso.setVisibility(View.GONE);// ocultamos el progreso
                        imagenTomada = magicalCamera.getPhoto();
                        imgImagen.setImageBitmap(imagenTomada);
                    }
                });
            }
        }).start();

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
