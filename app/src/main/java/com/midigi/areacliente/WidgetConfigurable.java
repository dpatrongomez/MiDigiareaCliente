package com.midigi.areacliente;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midigi.areacliente.modelo.UserData;
import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.servicios.Digi;
import com.midigi.areacliente.servicios.GetDigiData;
import com.midigi.areacliente.utils.Constantes;
import com.midigi.areacliente.utils.GestionarPreferences;
import com.midigi.areacliente.R;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetConfigurableConfigureActivity WidgetConfigurableConfigureActivity}
 */
public class WidgetConfigurable extends AppWidgetProvider {

    public static String REFRESH_ACTION = "MyCustomUpdate";
    private static UserData userData_actual;
    private static RemoteViews views;


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("digi","onReceive");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
if(intent.getAction()!=null) {
    if (intent.getAction().equals(REFRESH_ACTION)) {
        int appWidgetId = intent.getIntExtra("APP_WIDGET_ID", -1);

        getDatosWidget(context,true, appWidgetManager,appWidgetId);
        resizeWidget(context,appWidgetManager,appWidgetId);

        Toast.makeText(context, "Actualizando...", Toast.LENGTH_SHORT).show();
        //appWidgetManager.updateAppWidget(appWidgetIds, views);
    }else if(intent.getAction().equals(ACTION_APPWIDGET_UPDATE)){
        if(userData_actual!=null) {
            Bundle extras = intent.getExtras();
            int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            getDatosWidget(context, true, appWidgetManager, appWidgetId);
            resizeWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        getDatosWidget(context, false,appWidgetManager,appWidgetId);
        resizeWidget(context,appWidgetManager,appWidgetId);

    }

    public static void getDatosWidget(final Context context, final boolean actualizacion_manual, final AppWidgetManager appWidgetManager, final int appWidgetId){
        //contextApp=context;
        views = new RemoteViews(context.getPackageName(), R.layout.widget_configurable);
        GestionarPreferences gestionarPreferences=null;
        gestionarPreferences=gestionarPreferences.getPreferences();
        Usuario usuario = gestionarPreferences.getUsuarioWidget(context, appWidgetId);
        LinkedHashMap<String, Usuario> lista_usuarios = gestionarPreferences.getListaUsuarios(context);
        if (new Digi().isNetwork(context)) {

            if (usuario != null && lista_usuarios.get(usuario.getTelefono()) != null) {


                UserData userData = null;

                    try {

                        GetDigiData getDigiData=new GetDigiData() {
                            protected void onPostExecute(UserData userData) {
                                //Do your thing
                                userData_actual=userData;

                                actualizarWidget(appWidgetManager,appWidgetId,userData,context);
                                if(actualizacion_manual){
                                    Toast.makeText(context, "Consumo actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        getDigiData.execute(usuario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                views.setOnClickPendingIntent(R.id.refrescarWidget, getPenIntent(context, appWidgetId));
                views.setOnClickPendingIntent(R.id.refrescarWidgetPequeno, getPenIntent(context, appWidgetId));


            }
        }
    }
    public static void actualizarWidget(AppWidgetManager appWidgetManager, int appWidgetId, UserData userData, Context context){
         //views = new RemoteViews(context.getPackageName(), R.layout.widget_configurable);
        CharSequence widgetInternetText = "";
        CharSequence widgetMinutosText = "";
        CharSequence widgetNumTelf = "";
        CharSequence widgetEuros = "";
        CharSequence widgetFechaRenovacion = "";
        CharSequence widgetMbGb = "";

        if (userData.getInternet() != null && userData.getMinutos() != null) {

            try{
                double megasRestantes=Double.parseDouble(userData.getInternet());
                if(megasRestantes>1024){
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    widgetInternetText = numberFormat.format(megasRestantes/1024)+"";
                    widgetMbGb="GB";
                }else{
                    widgetInternetText = (int)megasRestantes+"";
                    widgetMbGb="MB";
                }
            }catch (Exception e){
                widgetInternetText = userData.getInternet();
                widgetMbGb="MB";
            }
            widgetFechaRenovacion = "Hasta: " + userData.getFecha_renovacion();
            widgetMinutosText = userData.getMinutos();
            widgetNumTelf = userData.getNum_telf();

            if (userData.getTipo_usuario().equals("Prepago") || userData.getTipo_usuario().equals("Prepago4G")) {
                widgetEuros = "Saldo: " + userData.getEuros() + "€";
            } else {
                widgetEuros = "Consumo: " + userData.getEuros()+"€";
            }
        } else {
            widgetInternetText = "Ocurrió un problema";

        }
        try {
            if (widgetMbGb.equals("MB")) {
                if (Integer.parseInt(widgetInternetText.toString()) < 500) {
                    views.setTextColor(R.id.internet_widget, Color.parseColor(Constantes.COLOR_PELIGRO_DATOS));
                } else {
                    views.setTextColor(R.id.internet_widget, Color.parseColor(Constantes.COLOR_ADVERTENCIA_DATOS));
                }
            }
            if (Integer.parseInt(widgetMinutosText.toString()) < 10) {
                views.setTextColor(R.id.minutos_widget, Color.parseColor(Constantes.COLOR_PELIGRO_DATOS));
            } else if(Integer.parseInt(widgetMinutosText.toString())<20) {
                views.setTextColor(R.id.minutos_widget, Color.parseColor(Constantes.COLOR_ADVERTENCIA_DATOS));
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }

            views.setTextViewText(R.id.internet_widget, widgetInternetText);
            views.setTextViewText(R.id.minutos_widget, widgetMinutosText);
            views.setTextViewText(R.id.num_telf_widget, widgetNumTelf);
            views.setTextViewText(R.id.euros_widget, widgetEuros);
            views.setTextViewText(R.id.fecha_widget, widgetFechaRenovacion);
            views.setTextViewText(R.id.megas_gigas, widgetMbGb);


            //appWidgetManager.updateAppWidget(appWidgetId, views);
            resizeWidget(context,appWidgetManager,appWidgetId);


    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d("digi","onUpdate");
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static private PendingIntent getPenIntent(Context context, int appWidgetID) {
        Intent intent = new Intent(context, WidgetConfigurable.class);
        intent.setAction(REFRESH_ACTION);
        intent.putExtra("APP_WIDGET_ID", appWidgetID);
        return PendingIntent.getBroadcast(context, appWidgetID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigurableConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        resizeWidget(context,appWidgetManager,appWidgetId);

    }

    public static void resizeWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_configurable);
        Bundle options=appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);

        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        if(maxHeight<=180){
            views.setTextViewTextSize(R.id.internet_widget, TypedValue.COMPLEX_UNIT_SP,15);
            views.setTextViewTextSize(R.id.minutos_widget, TypedValue.COMPLEX_UNIT_SP,15);
            views.setTextViewTextSize(R.id.euros_widget, TypedValue.COMPLEX_UNIT_SP,11);
            //MENOS DE UNA COLUMNA DE ANCHO
            if(minWidth<=80){
                views.setViewVisibility(R.id.refrescarWidget, View.GONE);
                views.setViewVisibility(R.id.refrescarWidgetPequeno,View.GONE);
                views.setViewVisibility(R.id.logo_widget, View.GONE);
                views.setViewVisibility(R.id.logo_widget_pequeno,View.GONE);
                if(userData_actual!=null) {
                    views.setTextViewText(R.id.euros_widget, userData_actual.getEuros() + "€");
                }
                    views.setTextViewTextSize(R.id.fecha_widget, TypedValue.COMPLEX_UNIT_SP, 7);
                    views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_SP, 9);
                    views.setTextViewText(R.id.texto_minutos, "min");

            //MENOS DE 1 COLUMNA DE ANCHO PERO MÁS ESTRECHO
                if(minWidth<=53){
                        views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_SP, 7);
                        views.setTextViewTextSize(R.id.internet_widget, TypedValue.COMPLEX_UNIT_SP, 13);
                        views.setTextViewTextSize(R.id.minutos_widget, TypedValue.COMPLEX_UNIT_SP, 13);

                }

            }
            //SI TIENE MÁS DE UNA COLUMNA DE ANCHO
            else{
                if(userData_actual!=null) {
                    /*if (userData_actual.getTipo_usuario().equals("Prepago")) {
                        views.setTextViewText(R.id.euros_widget, "Saldo: " + userData_actual.getEuros() + "€");
                    } else {
                        views.setTextViewText(R.id.euros_widget, "Consumo: " + userData_actual.getEuros() + "€");
                    }*/
                }
            }
        }
        //SI TIENE MÁS DE DOS FILAS DE ALTO
        else{
            views.setTextViewTextSize(R.id.internet_widget, TypedValue.COMPLEX_UNIT_SP,25);
            views.setTextViewTextSize(R.id.minutos_widget, TypedValue.COMPLEX_UNIT_SP,25);
            views.setTextViewTextSize(R.id.euros_widget, TypedValue.COMPLEX_UNIT_SP,16);
            views.setViewVisibility(R.id.euros_widget,View.VISIBLE);
        }

        if(minWidth<=157 && minWidth>80){
            views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_SP,11);
            views.setViewVisibility(R.id.refrescarWidget, View.GONE);
            views.setViewVisibility(R.id.refrescarWidgetPequeno,View.VISIBLE);
            views.setViewVisibility(R.id.logo_widget, View.GONE);
            views.setViewVisibility(R.id.logo_widget_pequeno,View.VISIBLE);
            views.setTextViewTextSize(R.id.fecha_widget, TypedValue.COMPLEX_UNIT_SP,9);
            appWidgetManager.updateAppWidget(appWidgetId, views);



        }else if(minWidth>157){
            views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_DIP,12);
            views.setViewVisibility(R.id.refrescarWidget, View.VISIBLE);
            views.setViewVisibility(R.id.refrescarWidgetPequeno,View.GONE);
            views.setViewVisibility(R.id.logo_widget, View.VISIBLE);
            views.setViewVisibility(R.id.logo_widget_pequeno,View.GONE);


            if(minWidth>=192){
                views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_DIP,15);
                views.setTextViewTextSize(R.id.fecha_widget, TypedValue.COMPLEX_UNIT_DIP,12);
            }else{
                views.setTextViewTextSize(R.id.num_telf_widget, TypedValue.COMPLEX_UNIT_DIP,12);
                views.setTextViewTextSize(R.id.fecha_widget, TypedValue.COMPLEX_UNIT_DIP,10);
            }



        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

