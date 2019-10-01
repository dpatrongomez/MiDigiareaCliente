package com.midigi.areacliente;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.utils.GestionarPreferences;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The configuration screen for the {@link WidgetConfigurable WidgetConfigurable} AppWidget.
 */
public class WidgetConfigurableConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.midigi.areacliente.WidgetConfigurable";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ListView listViewUsuarios;
    private GestionarPreferences gestionarPreferences;




    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetConfigurableConfigureActivity.this;

            // When the button is clicked, store the string locally
           // String widgetText = mAppWidgetText.getText().toString();
           // saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            WidgetConfigurable.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WidgetConfigurableConfigureActivity() {
        super();
    }



    static void deleteTitlePref(Context context, int appWidgetId) {


        SharedPreferences preferences=new SecurePreferences(context);
        SecurePreferences.Editor editor=((SecurePreferences) preferences).edit();
        editor.remove(GestionarPreferences.USUARIO_WIDGET+appWidgetId);

        editor.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        gestionarPreferences=gestionarPreferences.getPreferences();
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configurable_configure);
        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);

        listViewUsuarios =(ListView) findViewById((R.id.lista_usuarios));
        Gson gson=new Gson();
        LinkedHashMap<String, Usuario> lista_usuarios=gestionarPreferences.getListaUsuarios(this);
        List<Usuario> h;
        if(lista_usuarios!=null) {
            h = new ArrayList<>(lista_usuarios.values());

        }else{
            h=new ArrayList<>();

        }
        ArrayAdapter<Usuario> arrayAdapter = new ArrayAdapter<Usuario>(this, android.R.layout.simple_list_item_single_choice, h);
        listViewUsuarios.setAdapter(arrayAdapter);
        listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                gestionarPreferences.guardarUsuarioWidget((Usuario)clickItemObj,mAppWidgetId,WidgetConfigurableConfigureActivity.this);
            }
        });
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);


        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

       // mAppWidgetText.setText(loadTitlePref(WidgetConfigurableConfigureActivity.this, mAppWidgetId));
    }
}

