package com.midigi.areacliente;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.midigi.areacliente.modelo.Usuario;
import com.midigi.areacliente.utils.Constantes;
import com.midigi.areacliente.utils.GestionarPreferences;

import java.util.LinkedHashMap;

public class AreaClienteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    int contador=0;
    final public int PERMISO_CONCEDIDO=1;

    WebView webview;
    private LinkedHashMap<String,Usuario> lista_usuarios;
    private Usuario usuario_actual;
    Gson gson;
    private String lKeyFirst;
    private GestionarPreferences gestionarPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_cliente);
        gestionarPreferences=gestionarPreferences.getPreferences();
        if (!gestionarPreferences.getPrefSaltarMensaje(this)){
            advertenciaInicio();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("fb-messenger://user/340472976125022");

                Intent toMessenger= new Intent(Intent.ACTION_VIEW, uri);

                try {
                    startActivity(toMessenger);
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Snackbar.make(view, "Por favor, instala Facebook Messenger para contactar con Digi Mobil por esta vía.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISO_CONCEDIDO);

        }


        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISO_CONCEDIDO);

        }*/

       webview=findViewById(R.id.webView1);
        gson=new Gson();
        lista_usuarios=gestionarPreferences.getListaUsuarios(this);
        addUsuariosNavigationDrawer(lista_usuarios);
        if(getIntent().getStringExtra("Usuario")!=null || getIntent().getStringExtra("Contraseña")!=null){
            usuario_actual= new Usuario(getIntent().getStringExtra("Usuario"),getIntent().getStringExtra("Contraseña"));
        }else{
            if(!lista_usuarios.isEmpty()) {

                    lKeyFirst = lista_usuarios.keySet().iterator().next();

                usuario_actual = lista_usuarios.get(lKeyFirst);
            }
        }



        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
      //  setContentView(webview);
        cargarWebDigi();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            webview.setDownloadListener(new DownloadListener() {


                @Override
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, final String mimetype,
                                            long contentLength) {


                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    String cookie = CookieManager.getInstance().getCookie(url);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                           contentDisposition, contentDisposition, mimetype));;
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    request.addRequestHeader("Cookie", cookie);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Descargando archivo", //To notify the Client that the file is being downloaded
                            Toast.LENGTH_LONG).show();

                }


            });
        }

    }

    public void cargarWebDigi(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            CookieManager.getInstance().removeAllCookies(null);
        }

        webview.loadUrl("https://micuentadigi.digimobil.es/");

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                 String usuario=usuario_actual.getTelefono();
              String password=usuario_actual.getContraseña();



                    webview.loadUrl("javascript: var usuario=document.getElementById(\"username\").value ='" + usuario + "';");
                    webview.loadUrl("javascript: var uselessvar=document.getElementById(\"password\").value ='"+password+"';");
                    webview.loadUrl("javascript: var uselessvar=document.getElementById(\"remember_me\").checked ='true';");

                    //webview.loadUrl("javascript: var x = document.getElementsByTagName(\"button\")[1].click();");
                    webview.loadUrl("javascript: var x = document.querySelector('button[type=\"submit\"]').click();");

                    contador++;


            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("¿Estás seguro de que quieres salir?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                                CookieManager.getInstance().removeAllCookies(null);
                            }
                            finishAffinity();


                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void addUsuariosNavigationDrawer(LinkedHashMap<String,Usuario> lista_usuarios){
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();


        SubMenu menuGroup = m.findItem(0).getSubMenu();
        menuGroup.clear();
        for(String u : lista_usuarios.keySet()){
            try {
                int id = Integer.parseInt(u);

                menuGroup.add(0, id, 0, u).setIcon(R.drawable.baseline_account_circle_black_48dp);
            }catch (Exception e){
                Log.d("error",e.toString());
            }
        }
        menuGroup .add(0, Constantes.ID_AÑADIR_USUARIOS,0,"Añadir usuario").setIcon(R.drawable.person_add);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id_item = item.getItemId();
        switch (id_item) {
            case R.id.cerrar_sesion:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¡Atención!");
                builder.setMessage("¿Quieres eliminar este usuario?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id_click) {
                        dialog.dismiss();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                            CookieManager.getInstance().removeAllCookies(null);
                        }
                        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
                        Menu m = navView.getMenu().findItem(0).getSubMenu();
                        int id=Integer.parseInt(usuario_actual.getTelefono());
                        m.removeItem(id);
                        lista_usuarios.remove(usuario_actual.getTelefono());
                        guardarUsuarios(lista_usuarios);
                        if(!lista_usuarios.isEmpty()) {
                            lKeyFirst = lista_usuarios.keySet().iterator().next();
                            usuario_actual = lista_usuarios.get(lKeyFirst);
                            cargarWebDigi();
                        }else {
                            gestionarPreferences.guardarPrefSaltarInicio(false,AreaClienteActivity.this);
                            startActivity(new Intent(AreaClienteActivity.this, MainActivity.class));
                        }


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                AlertDialog mensaje_inicio=builder.create();
                mensaje_inicio.show();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.saldo) {
            pedirPermisoTel();
            String ussdCode = "*" + "130" + Uri.encode("#");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));}
        } else if (id == R.id.activar_bonos) {
            pedirPermisoTel();
            String ussdCode = "*" + "100" + Uri.encode("#");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));}

        } else if (id == R.id.internet_roaming) {
            pedirPermisoTel();
            String ussdCode = "*" + "148" + Uri.encode("#");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));}

        } else if (id == R.id.at_cliente) {
            pedirPermisoTel();
            String ussdCode = "1200";
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));}

        } else if (id == R.id.traspaso_saldo) {
            pedirPermisoTel();
            String ussdCode = "1215";
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));}


        } else if (id == R.id.acerca_de) {
            Intent i=new Intent(this,InfoActivity.class);
            startActivity(i);

        } else if(id== Constantes.ID_AÑADIR_USUARIOS){
            gestionarPreferences.guardarPrefSaltarInicio(false,AreaClienteActivity.this);
            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);
        }else if(id==R.id.donacion){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/GonzaloCuadrado"));
            startActivity(browserIntent);
        }else{
            usuario_actual=lista_usuarios.get(item.getTitle());
            cargarWebDigi();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void pedirPermisoTel(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISO_CONCEDIDO);

        }
    }

    public void advertenciaInicio (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Atención!");
        builder.setMessage(R.string.mensaje_inicio);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                gestionarPreferences.guardarPrefSaltarMensaje(false, getBaseContext());
            }
        });
        builder.setNegativeButton("No volver a mostrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                gestionarPreferences.guardarPrefSaltarMensaje(true, getBaseContext());
            }
        });

        AlertDialog mensaje_inicio=builder.create();
        mensaje_inicio.show();
    }

    public void guardarUsuarios(LinkedHashMap<String,Usuario> lista_usuarios){

        gestionarPreferences.guardarListaUsuarios(lista_usuarios,this);
    }


}
