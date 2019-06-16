package com.midigi.areacliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.servicios.CheckDigiUser;
import com.midigi.areacliente.servicios.Digi;
import com.midigi.areacliente.utils.GestionarPreferences;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private Map<String,Usuario> lista_usuarios;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gson=new Gson();
        lista_usuarios=gson.fromJson(GestionarPreferences.getUsuario(this),  new TypeToken<LinkedHashMap<String,Usuario>>(){}.getType());
    if(lista_usuarios!=null) {
        if (GestionarPreferences.getPrefSaltarInicio(this) && lista_usuarios.size() > 0) {

            Intent i = new Intent(MainActivity.this, AreaClienteActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finishAffinity();
        }
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
        Usuario usuario_actual=new Usuario(usuario,password);

        if(checkUser(usuario_actual)) {
            if (lista_usuarios == null) {
                lista_usuarios = new LinkedHashMap<>();

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lista_usuarios.putIfAbsent(usuario_actual.getTelefono(), usuario_actual);
            } else {
                lista_usuarios.put(usuario_actual.getTelefono(), usuario_actual);
            }

            GestionarPreferences.guardarUsuario(gson.toJson(lista_usuarios), this);
        /*GestionarPreferences.guardarUsuario(usuario,this);
        GestionarPreferences.guardarContraseña(password,this);*/
            Intent i = new Intent(MainActivity.this, AreaClienteActivity.class);
            i.putExtra("Usuario", usuario_actual.getTelefono());
            i.putExtra("Contraseña", usuario_actual.getContraseña());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finishAffinity();
        }
    }

    public boolean checkUser(Usuario u){
        String login_result="";
        boolean acceso_correcto=false;
        Digi digi=new Digi();
        if(digi.isNetwork(this)) {
            try {
                login_result = new CheckDigiUser().execute(u).get();
            } catch (Exception e) {

            }
            if (login_result.equals("\"ok\"")) {
                acceso_correcto = true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¡Atención!");
                builder.setMessage("Usuario o contraseña incorrectos");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });
                AlertDialog mensaje_inicio = builder.create();
                mensaje_inicio.show();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("¡Atención!");
            builder.setMessage("No estás conectado a internet");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                }
            });
            AlertDialog mensaje_inicio = builder.create();
            mensaje_inicio.show();
        }
        return acceso_correcto;
    }

    public void casilla_credenciales (View view){
        CheckBox casilla_credenciales=findViewById(R.id.casilla_inicio);
        if (view.getId()==R.id.casilla_inicio) {
            GestionarPreferences.guardarPrefSaltarInicio(casilla_credenciales.isChecked(), this);
        }
    }



}
