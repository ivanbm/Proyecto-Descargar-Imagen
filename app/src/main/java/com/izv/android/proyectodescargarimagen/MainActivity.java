package com.izv.android.proyectodescargarimagen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {

    private File archivo;
    private EditText etUrl, etNombre;
    private RadioButton rbPrivada, rbPublica;
    private Bitmap msg;
    private ImageView img;
    private String resp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*-----------------------------------------*/
    /*              METODOS PROPIOS            */
    /*-----------------------------------------*/

    public void guardar(View v){
        final String tUrl, tNom, ruta;
        etUrl = (EditText)findViewById(R.id.etUrl);
        etNombre = (EditText)findViewById(R.id.etNombre);
        rbPrivada = (RadioButton)findViewById(R.id.rbPrivada);
        rbPublica = (RadioButton)findViewById(R.id.rbPublica);
        img = (ImageView)findViewById(R.id.ivImagen);


        tUrl = etUrl.getText().toString();
        tNom = etNombre.getText().toString();

        if(rbPrivada.isChecked()){
            ruta = "privada";
        }else if(rbPublica.isChecked()){
            ruta = "publica";
        }else{
            ruta = "privada";
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    msg = descargarImagen(tUrl, ruta, tNom);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            img.setImageBitmap(msg);
                            tostada(resp);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();


    }


    public Bitmap descargarImagen(String Url, String ruta, String nombre){

        String filepath = null, mensaje;
        String extension = Url.substring(Url.lastIndexOf("."));

        if(extension.equals(".jpg") || extension.equals(".png") || extension.equals(".gif")) {
            if (nombre.equals("")) {
                nombre = Url.substring(Url.lastIndexOf("/") + 1, Url.lastIndexOf("."));
            }

            File destino = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), nombre + extension);

            if (ruta.equals("publica")) {
                destino = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nombre + extension);
            } else if (ruta.equals("privada")) {
                destino = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), nombre + extension);
            }


            try {

                URL url = new URL(Url);

                //creamos la conexion

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                //creamos el archivo
                destino.createNewFile();


                FileOutputStream fileOutput = new FileOutputStream(destino);
                InputStream inputStream = urlConnection.getInputStream();

                //tamaño total del archivo
                int tamanoTotal = urlConnection.getContentLength();

                //buffer para almacenar los datos.
                byte[] buffer = new byte[4096];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();


            } catch (Exception e) {
                System.out.println(e);
            }

            resp = "Guardada en: " + destino;
            Bitmap image = BitmapFactory.decodeFile("" + destino);
            Bitmap imgEscalada = Bitmap.createScaledBitmap(image, 300, 300, false);
            return imgEscalada;
        }else{
            tostada(""+R.string.msgExtension);
            return null;
        }
    }
    private void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}
