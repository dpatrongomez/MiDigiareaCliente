package com.midigi.areacliente;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (GestionarPreferences.getPrefSaltarInicio(this)){
            Intent i = new Intent(MainActivity.this, AreaClienteActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finishAffinity();
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CheckBox casilla_credenciales=findViewById(R.id.casilla_inicio);
        casilla_credenciales.setChecked(true);
        if (casilla_credenciales.isChecked()){
            GestionarPreferences.guardarPrefSaltarInicio(casilla_credenciales.isChecked(), this);
        }


    }

    public void acceso(View view) {
        EditText caja_user=findViewById(R.id.usuario);
        EditText caja_pass=findViewById(R.id.password);
        String usuario=caja_user.getText().toString();
        String password=caja_pass.getText().toString();
        GestionarPreferences.guardarUsuario(usuario,this);
        GestionarPreferences.guardarContraseña(password,this);
        Intent i = new Intent(MainActivity.this, AreaClienteActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finishAffinity();
    }

    public void casilla_credenciales (View view){
        CheckBox casilla_credenciales=findViewById(R.id.casilla_inicio);
        if (view.getId()==R.id.casilla_inicio) {
            GestionarPreferences.guardarPrefSaltarInicio(casilla_credenciales.isChecked(), this);
        }
    }



}
