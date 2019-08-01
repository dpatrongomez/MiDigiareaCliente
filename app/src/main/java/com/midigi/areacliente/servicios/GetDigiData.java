package com.midigi.areacliente.servicios;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.midigi.areacliente.modelo.UserData;
import com.midigi.areacliente.modelo.Usuario;
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

public class GetDigiData extends AsyncTask<Usuario,Void, UserData> {


    @Override
    protected UserData doInBackground(Usuario... usuarios) {
        Log.d("stop","doinbeackground");
        String usuario= usuarios[0].getTelefono();
        String pass=usuarios[0].getContraseña();
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

        // SACAR LOS MEGAS RESTANTES
       internet=encontrarDatos("<strong>(.+?)</strong> MB\n" +
               "\t\t\t\t\t\t<br>",response);


       // SACAR LOS MINUTOS

            minutos=encontrarDatos("<strong>(.+?) minutos nacionales </strong>",response);
            if(minutos.equals("-")) {
                if(response.contains("Combo")){
                    minutos=encontrarDatos("<strong>(.+?)</strong> minutos nacionales",response);
                }
            }

        // SACAR EL SALDO

        saldo=encontrarDatos("<strong>(.+?)€</strong>",response);


        // SACAR NÚMERO DE TELÉFONO
        num_telf=encontrarDatos("Número:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7\">\n" +
                "\t\t\t\t\t\t\t\t<span class=\"lead\"><strong>(.+?)</strong>",response);


        // SACAR FECHA RENOVACIÓN
        fecha_renovacion=encontrarDatos("Hasta el (.+?) a las",response);


        UserData u=new UserData(tipo_usuario,internet,minutos,saldo,num_telf,fecha_renovacion);
        return u;
    }


    private UserData crearUsuarioContrato(String response){
        String tipo_usuario="Contrato";
        String internet="-";
        String minutos="-";
        String consumo="-";
        String num_telf="";
        String fecha_renovacion="-";

        //SACAR MB DE INTERNET RESTANTES
       internet=encontrarDatos("<strong>(.+?) MB</strong> para navegar",response);


       // SACAR MINUTOS
        minutos=encontrarDatos("<strong>(.+?) minutos nacionales </strong>",response);
       if(minutos.equals("-")){
           if(response.contains("Combo")) {
               minutos = encontrarDatos("<strong>(.+?) minutos </strong> nacionales e internacionales", response);
               if (!minutos.equals("-")) {
                   minutos = minutos.substring(minutos.indexOf(">") + 1, minutos.lastIndexOf("minutos") - 1);
               }
           }
       }
       // SACAR CONSUMO

        consumo=encontrarDatos("Consumo actual:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7 lead\">\n" +
                "\t\t\t\t\t\t\t\t<strong>(.+?)€</strong>",response);


       //SACAR NÜMERO DE TELÉFONO
        num_telf=encontrarDatos("Número:\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t<div class=\"col-xs-7\">\n" +
                "\t\t\t\t\t\t\t\t<span class=\"lead\"><strong>(.+?)</strong></span>",response);

       // SACAR FECHA DE RENOVACIÓN
        fecha_renovacion=encontrarDatos("<p>Válidos hasta el próximo día (.+?).</p>",response);

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               fecha_renovacion=getFechaContrato(fecha_renovacion);
           }


        UserData u=new UserData(tipo_usuario,internet,minutos,consumo,num_telf,fecha_renovacion);
        return u;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFechaContrato(String fecha_renovacion){
        LocalDate fecha=LocalDate.now();
        int dia_renovacion;
        try{
            dia_renovacion=Integer.parseInt(fecha_renovacion);
        }catch (Exception e){
            dia_renovacion=0;
        }
        if(fecha.getDayOfMonth()>dia_renovacion){

            fecha= LocalDate.of(LocalDate.now().getYear(),fecha.getMonthValue(),dia_renovacion);
            fecha=fecha.plusMonths(1);
        }else{
            fecha= LocalDate.of(LocalDate.now().getYear(),fecha.getMonthValue(),dia_renovacion);
        }
        fecha_renovacion=fecha.format(DateTimeFormatter.ofPattern("dd/MM"));
        return fecha_renovacion;
    }

    private String encontrarDatos(String textoBuscado,String texto){
        String coincidencia="-";
        Pattern p=Pattern.compile(textoBuscado);
       Matcher m=p.matcher(texto);
        if(m.find()){
            coincidencia=m.group(1);

        }
        return coincidencia;
    }

}
