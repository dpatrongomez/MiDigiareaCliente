package com.midigi.areacliente;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.midigi.areacliente.modelo.Usuario;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetDigiData extends AsyncTask<Context,Void,Usuario> {

    @Override
    protected Usuario doInBackground(Context... contexts) {
        Log.d("stop","doinbeackground");
        String usuario=GestionarPreferences.getUsuario(contexts[0]);
        String pass=GestionarPreferences.getContraseña(contexts[0]);
    CookieJar mycookies=new MyCookieJar();
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(mycookies).build();

        RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), "user="+usuario+"&pass="+pass);

        Request request = new Request.Builder()
                .url("https://micuentadigi.digimobil.es/es/xhr-login")
                .post(body)
                .build();
        String respuesta="hola";
        try (Response response = client.newCall(request).execute()) {

            respuesta= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request hacerGet = new Request.Builder()
                .url("https://micuentadigi.digimobil.es/es/")
                .get()
                .build();
        try (Response response = client.newCall(hacerGet).execute()) {
            respuesta= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Usuario u=null;
        if(respuesta.contains("Contrato")){
            u=crearUsuarioContrato(respuesta);
        }else{
            u=crearUsuarioPrepago(respuesta);
        }

       // saldo=saldo.substring(saldo.indexOf(">")+1,saldo.lastIndexOf("<"));


        return u;
    }

    public Usuario crearUsuarioPrepago(String response){
        String internet="";
        String minutos="";
        String saldo="";
        String num_telf="";
        Pattern p= Pattern.compile("<strong>(.+?)</strong> MB\n" +
                "\t\t\t\t\t\t<br>");
        Matcher m=p.matcher(response);

        if(m.find()){
            internet=m.group(1);
        }
        p=Pattern.compile("<strong>(.+?) minutos nacionales </strong>");
        m=p.matcher(response);
        if(m.find()){
            minutos=m.group();
            minutos=minutos.substring(minutos.indexOf(">")+1,minutos.lastIndexOf("minutos")-1);
        }
        p=Pattern.compile("<strong>(.+?)€</strong>");
        m=p.matcher(response);
        if(m.find()){
            saldo=m.group();
            saldo=saldo.substring(saldo.indexOf(">")+1,saldo.lastIndexOf("€"));
        }
        p=Pattern.compile("Número:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7\">\n" +
                "\t\t\t\t\t\t\t\t<span class=\"lead\"><strong>(.+?)</strong>");
        m=p.matcher(response);
        if(m.find()){
            num_telf=m.group();
            num_telf=num_telf.substring(num_telf.indexOf("strong>")+7,num_telf.lastIndexOf("</strong>"));
        }
        Usuario u=new Usuario(internet,minutos,saldo,num_telf);
        return u;
    }

    public Usuario crearUsuarioContrato(String response){
        String internet="";
        String minutos="";
        String consumo="";
        String num_telf="";
        Pattern p=Pattern.compile("<strong>(.+?) MB</strong> para navegar");
       Matcher m=p.matcher(response);
        if(m.find()){
            internet=m.group();
            internet=internet.substring(internet.indexOf(">")+1,internet.lastIndexOf("MB")-1);
        }
        p=Pattern.compile("<strong>(.+?) minutos nacionales </strong>");
        m=p.matcher(response);
        if(m.find()){
            minutos=m.group();
            minutos=minutos.substring(minutos.indexOf(">")+1,minutos.lastIndexOf("minutos"));
        }
        Usuario u=new Usuario(internet,minutos,consumo,num_telf);
        return u;
    }


}
