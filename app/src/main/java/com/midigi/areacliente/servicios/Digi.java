package com.midigi.areacliente.servicios;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;



import java.io.ByteArrayInputStream;
import java.io.IOException;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Digi {
    private static String respuesta;

    public static void getDigiData(Context context){
       /* RequestParams params = new RequestParams();
        params.put("user", "677216391");
        params.put("pass", "chocoskate1234");
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore=new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);

        AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler() {
            @Override
            public void onStart(){
                respuesta="onstart";
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response="";
                for(int i=0;i<responseBody.length;i++){
                    response+=responseBody[i];
                }
                respuesta=response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String fallo="hola";
                respuesta=fallo;
            }
        };
        client.post("https://micuentadigi.digimobil.es/es/xhr-login", params, responseHandler);
        String cookie=myCookieStore.getCookies().get(0).getName()+"="+myCookieStore.getCookies().get(0).getValue();
        client.addHeader("Cookie",cookie);
        client.get("https://micuentadigi.digimobil.es/es/",responseHandler);*/

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), "user=643559200&pass=chocoskate1234");

        Request request = new Request.Builder()
                .url("https://micuentadigi.digimobil.es/es/xhr-login")
                .post(body)
                .build();
        String respuesta;
        try (Response response = client.newCall(request).execute()) {
            respuesta= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public  boolean isNetwork(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public  boolean isConnectedNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public static String setResponse(String response){
        respuesta=response;
        return response;
    }
}
