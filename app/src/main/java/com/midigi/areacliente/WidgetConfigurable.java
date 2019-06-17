package com.midigi.areacliente;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midigi.areacliente.modelo.UserData;
import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.servicios.Digi;
import com.midigi.areacliente.servicios.GetDigiData;
import com.midigi.areacliente.utils.GestionarPreferences;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetConfigurableConfigureActivity WidgetConfigurableConfigureActivity}
 */
public class WidgetConfigurable extends AppWidgetProvider {

    public static String REFRESH_ACTION = "MyCustomUpdate";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equalsIgnoreCase(REFRESH_ACTION)) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_configurable);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetConfigurable.class));
            int appWidgetId = intent.getIntExtra("APP_WIDGET_ID", -1);
            onUpdate(context, appWidgetManager, appWidgetIds);


            Toast.makeText(context, "Consumo actualizado", Toast.LENGTH_SHORT).show();
            //appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetInternetText = "";
        CharSequence widgetMinutosText = "";
        CharSequence widgetNumTelf = "";
        CharSequence widgetEuros = "";
        CharSequence widgetFechaRenovacion = "";
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_configurable);
        Usuario usuario = GestionarPreferences.getUsuarioWidget(context, appWidgetId);
        Gson gson = new Gson();
        LinkedHashMap<String, Usuario> lista_usuarios = gson.fromJson(GestionarPreferences.getUsuario(context), new TypeToken<LinkedHashMap<String, Usuario>>() {
        }.getType());

            if (new Digi().isNetwork(context)) {
                if (usuario != null && lista_usuarios.get(usuario.getTelefono()) != null) {
                    GetDigiData getDigiData = new GetDigiData();
                    UserData userData = null;

                    try {
                        userData = getDigiData.execute(usuario).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (userData.getInternet() != null && userData.getMinutos() != null) {
                        widgetInternetText = userData.getInternet();
                        widgetFechaRenovacion = "Hasta: " + userData.getFecha_renovacion();
                        widgetMinutosText = userData.getMinutos() + " ";
                        widgetNumTelf = userData.getNum_telf();
                        if (userData.getTipo_usuario().equals("Prepago")) {
                            widgetEuros = "Saldo: " + userData.getEuros() + "€";
                        } else {
                            widgetEuros = "Consumo: " + userData.getEuros() + "€";
                        }
                    } else {
                        widgetInternetText = "Ocurrió un problema";
                    }
                } else {
                    widgetNumTelf = "Inicia sesión";
                }

                // Construct the RemoteViews object*/

                views.setTextViewText(R.id.internet_widget, widgetInternetText);
                views.setTextViewText(R.id.minutos_widget, widgetMinutosText);
                views.setTextViewText(R.id.num_telf_widget, widgetNumTelf);
                views.setTextViewText(R.id.euros_widget, widgetEuros);
                views.setTextViewText(R.id.fecha_widget, widgetFechaRenovacion);

            }

        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//

       /*
        //intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentUpdate.setAction(REFRESH_ACTION);

//Update the current widget instance only, by creating an array that contains the widget’s unique ID//

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

//Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//

        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

//Send the pending intent in response to the user tapping the ‘Update’ TextView//

       // views.setOnClickPendingIntent(R.id.refrescarWidget, pendingUpdate);*/
        views.setOnClickPendingIntent(R.id.refrescarWidget, getPenIntent(context, appWidgetId));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static private PendingIntent getPenIntent(Context context, int appWidgetID) {
        Intent intent = new Intent(context, WidgetConfigurable.class);
        intent.setAction(REFRESH_ACTION);
        intent.putExtra("APP_WIDGET_ID", appWidgetID);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

