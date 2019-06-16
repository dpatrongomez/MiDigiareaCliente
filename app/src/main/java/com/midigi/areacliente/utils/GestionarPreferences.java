package com.midigi.areacliente.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.midigi.areacliente.modelo.Usuario;
import com.securepreferences.SecurePreferences;

public class GestionarPreferences {

        public static final String NOMBRE_FICHERO = "usuario_preferences";
        public static final String USUARIO = "usuario";
        public static final String USUARIO_WIDGET="usuario_widget";
        public static final String CONTRASEÑA = "contraseña";
        public static final String CLAVE_SALTAR_INTRO = "saltar_intro";
    public static final String CLAVE_SALTAR_MENSAJE = "saltar_mensaje";


        //metodo que guarda las preferencias de la checkbox
        public static void guardarUsuario(String nombre_usuario , Context context) {
            SharedPreferences preferences=new SecurePreferences(context);
            SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
            editor.putString(USUARIO, nombre_usuario);
            editor.commit();
        }

        //metodo que recupera las preferencias de la checkbox
        public static String getUsuario(Context context){
            String valor = "";
            SharedPreferences preferences=new SecurePreferences(context);
            valor=preferences.getString(USUARIO,null);

            return valor;
        }

    public static void guardarUsuarioWidget(Usuario usuario,int appWidget, Context context) {
        SharedPreferences preferences=new SecurePreferences(context);
        SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
        Gson gson=new Gson();

        editor.putString(USUARIO_WIDGET+appWidget, gson.toJson(usuario));
        editor.commit();
    }

    public static Usuario getUsuarioWidget(Context context, int appWidget){
        String valor = "";
        SecurePreferences preferences=new SecurePreferences(context);
        valor=preferences.getString(USUARIO_WIDGET+appWidget,null);
        Gson gson=new Gson();
        Usuario u=gson.fromJson(valor,Usuario.class);

        return u;
    }

    public static void guardarContraseña(String contraseña , Context context) {
        SharedPreferences preferences=new SecurePreferences(context);
        SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
        editor.putString(CONTRASEÑA, contraseña);
        editor.commit();
    }

        //metodo que recupera las preferencias de la checkbox
        public static String getContraseña(Context context){
            String valor = "";
            SharedPreferences preferences=new SecurePreferences(context);
            valor=preferences.getString(CONTRASEÑA,null);

            return valor;
        }




    public static void guardarPrefSaltarInicio(boolean activo , Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CLAVE_SALTAR_INTRO, activo);
        editor.commit();
    }

    //metodo que recupera las preferencias de la checkbox
    public static boolean getPrefSaltarInicio(Context context){

        boolean valor = false;

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        valor = sharedPreferences.getBoolean(CLAVE_SALTAR_INTRO,false);

        return valor;
    }

    public static void guardarPrefSaltarMensaje(boolean activo , Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CLAVE_SALTAR_MENSAJE, activo);
        editor.commit();
    }

    //metodo que recupera las preferencias de la checkbox
    public static boolean getPrefSaltarMensaje(Context context){

        boolean valor = false;

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        valor = sharedPreferences.getBoolean(CLAVE_SALTAR_MENSAJE,false);

        return valor;
    }
}
