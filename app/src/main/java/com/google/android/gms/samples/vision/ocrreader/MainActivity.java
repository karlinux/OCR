/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private Button btnGuardar;
    private EditText etNombre, etPaterno, etMaterno, etDomicilio, etEstado, etMunicipio, etLocalidad;

    Handler_sqlite base;
    private ArrayList<String> listItems;
    ListView lista;


    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        base = new Handler_sqlite(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etPaterno = (EditText) findViewById(R.id.etPaterno);
        etMaterno = (EditText) findViewById(R.id.etMaterno);
        etDomicilio = (EditText) findViewById(R.id.etDomicilio);
        etEstado = (EditText) findViewById(R.id.etEstado);
        etMunicipio = (EditText) findViewById(R.id.etMunicipio);
        etLocalidad = (EditText) findViewById(R.id.etLocalidad);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        lista = (ListView) findViewById(R.id.lista);
        listItems = new ArrayList<>();
        adaptador();
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_text).setOnClickListener(this);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                base.abrir();
                base.insertarRegistro(
                        etNombre.getText().toString(),
                        etPaterno.getText().toString(),
                        etMaterno.getText().toString(),
                        etDomicilio.getText().toString(),
                        etEstado.getText().toString(),
                        etMunicipio.getText().toString(),
                        etLocalidad.getText().toString());
                base.cerrar();
                etNombre.setText("");
                etPaterno.setText("");
                etMaterno.setText("");
                etDomicilio.setText("");
                etEstado.setText("");
                etMunicipio.setText("");
                etLocalidad.setText("");
                adaptador();
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    //statusMessage.setText(R.string.ocr_success);
                    text = text.replaceAll("\n","---");

                    String[]  textoNombre = new String[]{""};
                    textoNombre = text.split( "NOMBRE");

                    //Log.e(TAG, "onActivityResultado: " + textoNombre.length );
                    if(textoNombre.length > 1){
                    text = textoNombre[1];
                    //Log.e(TAG, "onActivityResultado: " + textoNombre.length );
                    }

                    String[] textoDomicilio = new String[]{""};
                    textoDomicilio = text.split( "DOMICILIO");

                    if(textoDomicilio.length > 1){
                        text = textoDomicilio[1];
                        String [] nombreCompleto = textoDomicilio[0].split("---");
                        if(nombreCompleto.length < 1){
                            Toast.makeText(this, "" + textoDomicilio[0], Toast.LENGTH_SHORT).show();
                            etPaterno.setText(nombreCompleto[1]);
                        }

                        if(nombreCompleto.length>2){
                            Toast.makeText(this, "" + textoDomicilio[0], Toast.LENGTH_SHORT).show();
                            etPaterno.setText(nombreCompleto[1]);
                            etMaterno.setText(nombreCompleto[2]);
                        }

                        if(nombreCompleto.length>3){
                            Toast.makeText(this, "" + textoDomicilio[0], Toast.LENGTH_SHORT).show();
                            etPaterno.setText(nombreCompleto[1]);
                            etMaterno.setText(nombreCompleto[2]);
                            etNombre.setText(nombreCompleto[3]);
                        }

                    }


                    if(textoDomicilio.length > 1){
                        String [] domicilioCompleto = textoDomicilio[1].split("---");
                        if(domicilioCompleto.length>=4) {
                            etDomicilio.setText(domicilioCompleto[1] + " " +
                                    domicilioCompleto[2] + " " +
                                    domicilioCompleto[3]);
                        }
                    }
    /////////////////////////    Estado

                    String[] textoEstado = new String[]{""};
                    textoEstado = text.split( "ESTADO");

                    if(textoEstado.length > 1){
                        text = textoEstado[1];
                        String[] estadoCompleto = textoEstado[1].split("---");
                        etEstado.setText(estadoCompleto[0]);
                    }
                    //textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);

                    /////////////////////   Localidad

                    String[] textoLocalidad = new String[]{""};
                    textoLocalidad = text.split( "LOCALIDAD");

                    if(textoLocalidad.length > 1){
                        text = textoLocalidad[1];
                        String[] estadoCompleto = textoLocalidad[1].split("---");
                        etLocalidad.setText(estadoCompleto[0]);
                    }


                    /////////////////////   Municipio

                    String[]  textoMunicipio = new String[]{""};
                    textoMunicipio = text.split( "MUNICIPIO");

                    if(textoMunicipio.length > 1){
                        text = textoMunicipio[1];
                        String[] estadoCompleto = textoMunicipio[1].split("---");
                        etMunicipio.setText(estadoCompleto[0]);
                    }

                    textoMunicipio = text.split( "MUNICIPI0");

                    if(textoMunicipio.length > 1){
                        text = textoMunicipio[1];
                        String[] estadoCompleto = textoMunicipio[1].split("---");
                        etMunicipio.setText(estadoCompleto[0]);
                    }
                    //textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);

                } else {
//                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                textValue.setText(String.format(getString(R.string.ocr_error),  CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void adaptador(){
        base.abrir();
        listItems.clear();
        Cursor c = base.getDatos();
        //Array list y adaptador
        int lastitem = c.getCount();
        if(lastitem > 0){

            while (c.moveToNext()){
                listItems.add( "NOMBRE " + c.getString(0) + " " + c.getString(1) + " " + c.getString(2) +
                        " \nDOMICILIO " + c.getString(3) +
                        " \nESTADO " + c.getString(4) +
                        " \nMUNICIPIO " + c.getString(5) +
                        " \nLOCALIDAD " + c.getString(6));
            }

        }
        base.cerrar();
        ArrayAdapter adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        lista.setAdapter(adaptador);
        lista.setSelection(lastitem);

    }
}
