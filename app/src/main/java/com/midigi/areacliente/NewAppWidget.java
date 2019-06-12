package com.midigi.areacliente;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.servicios.Digi;

import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {





    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetInternetText="";
        CharSequence widgetMinutosText="";
        CharSequence widgetNumTelf="";
        CharSequence widgetEuros="";
        CharSequence widgetFechaRenovacion="";
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

    if(new Digi().isNetwork(context)) {
        GetDigiData g = new GetDigiData();
        Usuario u = null;
        try {
            u = g.execute(context).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (u.getInternet() != null && u.getMinutos() != null) {
            widgetInternetText = u.getInternet();
            widgetFechaRenovacion = "Hasta: " + u.getFecha_renovacion();
            widgetMinutosText = u.getMinutos() + " ";
            widgetNumTelf = u.getNum_telf();
            if (u.getTipo_usuario().equals("Prepago")) {
                widgetEuros = "Saldo: " + u.getEuros() + "€";
            } else {
                widgetEuros = "Consumo: " + u.getEuros() + "€";
            }
        } else {
            widgetInternetText = "Ocurrió un problema";
        }

        // Construct the RemoteViews object*/

        views.setTextViewText(R.id.internet_widget, widgetInternetText);
        views.setTextViewText(R.id.minutos_widget, widgetMinutosText);
        views.setTextViewText(R.id.num_telf_widget, widgetNumTelf);
        views.setTextViewText(R.id.euros_widget, widgetEuros);
        views.setTextViewText(R.id.fecha_widget, widgetFechaRenovacion);

    }
        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//

        Intent intentUpdate = new Intent(context, NewAppWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

//Update the current widget instance only, by creating an array that contains the widget’s unique ID//

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

//Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//

        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

//Send the pending intent in response to the user tapping the ‘Update’ TextView//

        views.setOnClickPendingIntent(R.id.refrescarWidget, pendingUpdate);





        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //Toast.makeText(context, "Consumo actualizado", Toast.LENGTH_SHORT).show();
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

