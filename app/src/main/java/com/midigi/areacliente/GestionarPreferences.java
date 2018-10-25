package com.midigi.areacliente;

import android.content.Context;
import android.content.SharedPreferences;

public class GestionarPreferences {

        public static final String NOMBRE_FICHERO = "usuario_preferences";
        public static final String USUARIO = "usuario";
        public static final String CONTRASEÑA = "contraseña";
        public static final String CLAVE_SALTAR_INTRO = "saltar_intro";


        //metodo que guarda las preferencias de la checkbox
        public static void guardarUsuario(String nombre_usuario , Context context) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USUARIO, nombre_usuario);
            editor.commit();
        }

        //metodo que recupera las preferencias de la checkbox
        public static String getUsuario(Context context){

            String valor = "";

            SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            valor = sharedPreferences.getString(USUARIO,null);

            return valor;
        }

    public static void guardarContraseña(String contraseña , Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTRASEÑA, contraseña);
        editor.commit();
    }

        //metodo que recupera las preferencias de la checkbox
        public static String getContraseña(Context context){

            String valor = "";

            SharedPreferences sharedPreferences = context.getSharedPreferences(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            valor = sharedPreferences.getString(CONTRASEÑA,null);

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
}
