package com.midigi.areacliente.servicios;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.midigi.areacliente.modelo.UserData;
import com.midigi.areacliente.utils.GestionarPreferences;
import com.midigi.areacliente.utils.MyCookieJar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetDigiData extends AsyncTask<Context,Void, UserData> {


    @Override
    protected UserData doInBackground(Context... contexts) {
        Log.d("stop","doinbeackground");
        String usuario= GestionarPreferences.getUsuario(contexts[0]);
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
        UserData u=null;
        if(respuesta.contains("Contrato")){
            u=crearUsuarioContrato(respuesta);
        }else{
            u=crearUsuarioPrepago(respuesta);
        }

       // saldo=saldo.substring(saldo.indexOf(">")+1,saldo.lastIndexOf("<"));


        return u;
    }

    public UserData crearUsuarioPrepago(String response){
        String tipo_usuario="Prepago";
        String internet="-";
        String minutos="-";
        String saldo="-";
        String num_telf="";
        String fecha_renovacion="-";
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
        p=Pattern.compile("Hasta el (.+?) a las");
        m=p.matcher(response);
        if(m.find()){
            fecha_renovacion=m.group();
            fecha_renovacion=fecha_renovacion.substring(fecha_renovacion.indexOf("el")+3,fecha_renovacion.indexOf("a las")-1);
        }
        UserData u=new UserData(tipo_usuario,internet,minutos,saldo,num_telf,fecha_renovacion);
        return u;
    }


    public UserData crearUsuarioContrato(String response){
        String tipo_usuario="Contrato";
        String internet="-";
        String minutos="-";
        String consumo="-";
        String num_telf="";
        String fecha_renovacion="-";
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
        p=Pattern.compile("Consumo actual:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7 lead\">\n" +
                "\t\t\t\t\t\t\t\t<strong>(.+?)€</strong>");
        m=p.matcher(response);
        if(m.find()){
            consumo=m.group();
            consumo=consumo.substring(consumo.indexOf("strong>")+7,consumo.lastIndexOf("€"));
        }
        p=Pattern.compile("Número:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7\">\n" +
                "\t\t\t\t\t\t\t\t<span class=\"lead\"><strong>(.+?)</strong></span>");
        m=p.matcher(response);
        if(m.find()){
            num_telf=m.group();
            num_telf=num_telf.substring(num_telf.indexOf("strong>")+7,num_telf.lastIndexOf("</strong>"));
        }
        p=Pattern.compile("<p>Válidos hasta el próximo día (.+?).</p>");
        m=p.matcher(response);
        if(m.find()){
            fecha_renovacion=m.group();
            fecha_renovacion=fecha_renovacion.substring(fecha_renovacion.indexOf("día")+4,fecha_renovacion.lastIndexOf("."));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fecha_renovacion=getFechaContrato(fecha_renovacion);
            }

        }
        UserData u=new UserData(tipo_usuario,internet,minutos,consumo,num_telf,fecha_renovacion);
        return u;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFechaContrato(String fecha_renovacion){
        LocalDate fecha=LocalDate.now();
        int dia_renovacion;
        try{
            dia_renovacion=Integer.parseInt(fecha_renovacion);
        }catch (Exception e){
            dia_renovacion=0;
        }
        if(fecha.getDayOfMonth()>dia_renovacion){
            fecha.plusMonths(1);
            fecha= LocalDate.of(LocalDate.now().getYear(),fecha.getMonthValue(),dia_renovacion);
        }else{
            fecha= LocalDate.of(LocalDate.now().getYear(),fecha.getMonthValue(),dia_renovacion);
        }
        fecha_renovacion=fecha.format(DateTimeFormatter.ofPattern("dd/MM"));
        return fecha_renovacion;
    }


}
