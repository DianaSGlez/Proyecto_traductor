package com.example.traductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traductor.Modelo.Idioma;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText et_idioma_origen;
    TextView tv_idioma_destino;
    MaterialButton btn_elegir_idioma, btn_idioma_elegido, btn_traducir;
    private ProgressDialog progressDialog;

    private ArrayList<Idioma> IdiomasArrayList;

    private static final String REGISTROS = "Mis_Registros";

    private String codigo_idioma_origen = "es";
    private String titulo_idioma_origen = "Español";

    private String codigo_idioma_destino = "en";

    private String titulo_idioma_destino = "Inglés";


    private TranslatorOptions translatorOptions;
    private Translator translator;
    private String Texto_idioma_origen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicializarVistas();
        IdiomasDisponibles();

        btn_elegir_idioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Elegir idioma", Toast.LENGTH_SHORT).show();
                EleginIdiomaOrigen();
            }
        });
        btn_idioma_elegido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Idioma elegido", Toast.LENGTH_SHORT).show();
                ElegirIdiomaDestino();
            }
        });
        btn_traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Traducir", Toast.LENGTH_SHORT).show();
                ValidarDatos();
            }
        });
    }

    private void InicializarVistas(){
        et_idioma_origen = findViewById(R.id.et_idioma_origen);
        tv_idioma_destino = findViewById(R.id.tv_idioma_destino);
        btn_elegir_idioma = findViewById(R.id.btn_elegir_idioma);
        btn_idioma_elegido = findViewById(R.id.btn_idioma_elegido);
        btn_traducir = findViewById(R.id.btn_traducir);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void IdiomasDisponibles(){
        IdiomasArrayList = new ArrayList<>();
        List<String> ListaCodigoIdioma = TranslateLanguage.getAllLanguages();

        for (String codigo_lenguaje : ListaCodigoIdioma){
            String titulo_lenguaje = new Locale(codigo_lenguaje).getDisplayLanguage();

            //Log.d(REGISTROS, "IdiomasDisponibles: codigo_lenguaje: "+ codigo_lenguaje);
            //Log.d(REGISTROS, "IdiomasDisponibles: titulo_lenguaje: "+ titulo_lenguaje);

            Idioma modeloIdioma = new Idioma(codigo_lenguaje, titulo_lenguaje);
            IdiomasArrayList.add(modeloIdioma);
        }
    }

    private  void EleginIdiomaOrigen(){
        PopupMenu popupmenu = new PopupMenu(this, btn_elegir_idioma);
        for (int i=0 ; i<IdiomasArrayList.size() ; i++){
            popupmenu.getMenu().add(Menu.NONE, i, i, IdiomasArrayList.get(i).getTitulo_idioma());
        }

        popupmenu.show();

        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_origen = IdiomasArrayList.get(position).getCodigo_idioma();
                titulo_idioma_origen = IdiomasArrayList.get(position).getTitulo_idioma();

                btn_elegir_idioma.setText(titulo_idioma_origen);
                et_idioma_origen.setHint("Ingrese texto en: "+titulo_idioma_origen);

                Log.d(REGISTROS, "onMenuItenClick: codigo_idioma_origen: "+ codigo_idioma_origen);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_origen: "+ titulo_idioma_origen);

                return false;
            }
        });
    }

    private void ElegirIdiomaDestino(){
        PopupMenu popupmenu = new PopupMenu(this, btn_idioma_elegido);
        for (int i=0 ; i<IdiomasArrayList.size() ; i++){
            popupmenu.getMenu().add(Menu.NONE, i, i, IdiomasArrayList.get(i).getTitulo_idioma());
        }

        popupmenu.show();

        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_destino = IdiomasArrayList.get(position).getCodigo_idioma();
                titulo_idioma_destino = IdiomasArrayList.get(position).getTitulo_idioma();

                btn_idioma_elegido.setText(titulo_idioma_destino);

                Log.d(REGISTROS, "onMenuItenClick: codigo_idioma_destino: "+ codigo_idioma_destino);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_destino: "+ titulo_idioma_destino);

                return false;
            }
        });
    }

    private void ValidarDatos() {
        Texto_idioma_origen = et_idioma_origen.getText().toString().trim();
        Log.d(REGISTROS, "ValidarDatos: Texto_idioma_origen"+ Texto_idioma_origen);

        if(Texto_idioma_origen.isEmpty()){
            Toast.makeText(this, "Ingrese texto", Toast.LENGTH_SHORT).show();
        }else{
            TraducirTexto();
        }
    }

    private void TraducirTexto() {
        progressDialog.setMessage("Procesando");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(codigo_idioma_origen)
                .setTargetLanguage(codigo_idioma_destino)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Los paquetes de traducción se descargaron con éxito
                        Log.d(REGISTROS, "onSuccess: El paquete se ha descargado con éxito");
                        progressDialog.setMessage("Traduciendo texto");

                        translator.translate(Texto_idioma_origen)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String texto_traducido) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onSuccess: texto_traducido"+texto_traducido);
                                        tv_idioma_destino.setText(texto_traducido);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onFailure"+e);
                                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Los paquetes no se descargaron
                        progressDialog.dismiss();
                        Log.d(REGISTROS, "onFailure"+e);
                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}