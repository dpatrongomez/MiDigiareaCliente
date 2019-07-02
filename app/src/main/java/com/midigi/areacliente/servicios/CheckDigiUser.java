package com.midigi.areacliente.servicios;

import android.content.Context;
import android.os.AsyncTask;

import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.utils.GestionarPreferences;
import com.midigi.areacliente.utils.MyCookieJar;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckDigiUser extends AsyncTask<Usuario,Void,String> {
    @Override
    protected String doInBackground(Usuario... usuarios) {
        String respuesta=null;
        String usuario= usuarios[0].getTelefono();
        String pass=usuarios[0].getContraseña();
        CookieJar mycookies=new MyCookieJar();
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(mycookies).build();

        RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), "user="+usuario+"&pass="+pass);

        Request request = new Request.Builder()
                .url("https://micuentadigi.digimobil.es/es/xhr-login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            respuesta= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            respuesta="\"ok\"";
        }
        return respuesta;
    }
}
