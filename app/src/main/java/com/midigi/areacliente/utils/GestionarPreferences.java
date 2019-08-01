package com.midigi.areacliente.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midigi.areacliente.modelo.Usuario;
import com.securepreferences.SecurePreferences;

import java.util.LinkedHashMap;

public class GestionarPreferences {

        private static GestionarPreferences preferencesInstance;
        public static final String NOMBRE_FICHERO = "usuario_preferences";
        public static final String USUARIO = "usuario";
        public static final String USUARIO_WIDGET="usuario_widget";
        public static final String CONTRASEÑA = "contraseña";
        public static final String CLAVE_SALTAR_INTRO = "saltar_intro";
    public static final String CLAVE_SALTAR_MENSAJE = "saltar_mensaje";
    private Gson gson;


        private GestionarPreferences(){
            gson=new Gson();
        }

        public static GestionarPreferences getPreferences(){
            if (preferencesInstance==null){
                preferencesInstance=new GestionarPreferences();
            }
            return preferencesInstance;
        }
        //metodo que guarda las preferencias de la checkbox
        public  void guardarListaUsuarios(LinkedHashMap<String,Usuario> listaUsuarios , Context context) {
            String jsonUsuario=gson.toJson(listaUsuarios);
            SharedPreferences preferences=new SecurePreferences(context);
            SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
            editor.putString(USUARIO, jsonUsuario);
            editor.commit();


        }

        //metodo que recupera las preferencias de la checkbox
        public  LinkedHashMap<String,Usuario> getListaUsuarios(Context context){
            LinkedHashMap<String,Usuario> listaUsuarios=null;
            String valor = "";
            SharedPreferences preferences=new SecurePreferences(context);
            valor=preferences.getString(USUARIO,null);
            listaUsuarios=gson.fromJson(valor,new TypeToken<LinkedHashMap<String,Usuario>>(){}.getType());

            return listaUsuarios;
        }

    public void guardarUsuarioWidget(Usuario usuario,int appWidget, Context context) {
        SharedPreferences preferences=new SecurePreferences(context);
        SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
        Gson gson=new Gson();

        editor.putString(USUARIO_WIDGET+appWidget, gson.toJson(usuario));
        editor.commit();
    }

    public Usuario getUsuarioWidget(Context context, int appWidget){
        String valor = "";
        SecurePreferences preferences=new SecurePreferences(context);
        valor=preferences.getString(USUARIO_WIDGET+appWidget,null);
        Gson gson=new Gson();
        Usuario u=gson.fromJson(valor,Usuario.class);

        return u;
    }

    public void guardarContraseña(String contraseña , Context context) {
        SharedPreferences preferences=new SecurePreferences(context);
        SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
        editor.putString(CONTRASEÑA, contraseña);
        editor.commit();
    }

        //metodo que recupera las preferencias de la checkbox
        public String getContraseña(Context context){
            String valor = "";
            SharedPreferences preferences=new SecurePreferences(context);
            valor=preferences.getString(CONTRASEÑA,null);

            return valor;
        }




    public void guardarPrefSaltarInicio(boolean activo , Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CLAVE_SALTAR_INTRO, activo);
        editor.commit();
    }

    //metodo que recupera las preferencias de la checkbox
    public boolean getPrefSaltarInicio(Context context){

        boolean valor = false;

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        valor = sharedPreferences.getBoolean(CLAVE_SALTAR_INTRO,false);

        return valor;
    }

    public void guardarPrefSaltarMensaje(boolean activo , Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CLAVE_SALTAR_MENSAJE, activo);
        editor.commit();
    }

    //metodo que recupera las preferencias de la checkbox
    public boolean getPrefSaltarMensaje(Context context){

        boolean valor = false;

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        valor = sharedPreferences.getBoolean(CLAVE_SALTAR_MENSAJE,false);

        return valor;
    }
}
