package com.example.myapplication;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    int mode=0;
    public static String latitude;
    public static String longitude;
    public static String app_version = "1.1";

    public static int clicked =0;

    public static boolean connected;
    SessionManager sessionManager;
    LocationManager locationManager;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    // buttons
    Button scan_nfc;
    Button read_nfc;
    Button scan_ticket;
    Button send;
    // TextView
    TextView nfc_qr_tv;
    TextView nfc_uuid_tv;
    TextView ticket_tv;
    TextView sqliTest;
    HashMap<String , String> user = null;
    DBHelper DB;
    ArrayList<Mapping> list;

    ActivityResultLauncher<String[]> locationPermissionRequest;
    BroadcastReceiver broadcastReceiver;

    // bg thread
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        broadcastReceiver = new CheckNetwork();
        //registerNetworkBroadcast();

        locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);

                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            System.out.println("&&&& fine Location granted");
                            getLocation();
                            //System.out.println("########### lat:"+latitude);
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            System.out.println("&&&& coarse Location granted");
                            getLocation();
                        }else {
                            System.out.println("&&&& No location granted");
                            //showAlert("","L'application a besoin de permission pour avoir la position GPS du telephone.");
                            //finishAffinity();
                            launchPermission();
                        }
                    }
            );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // location permission
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // initialize thread
        thread = null;


        //new session
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        // buttons
        scan_nfc = (Button)findViewById(R.id.scan_nfc);
        read_nfc = (Button)findViewById(R.id.read_nfc);
        scan_ticket = (Button)findViewById(R.id.scan_ticket_btn);
        send = (Button)findViewById(R.id.send);

        // textviews
        nfc_qr_tv = (TextView)findViewById(R.id.nfc_qr_code);
        nfc_uuid_tv = (TextView)findViewById(R.id.nfc_uuid);
        ticket_tv = (TextView)findViewById(R.id.ticket_qr_code);

        //sqliTest = (TextView)findViewById(R.id.sqlite);
        // datbase
        DB = new DBHelper(this);



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_icon);

        // click listeners
        scan_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=1;
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        HomeActivity.this
                );
                // set prompt text
                intentIntegrator.setPrompt("For flash use volume up key");
                // set beep
                intentIntegrator.setBeepEnabled(true);
                // Locked orientation
                intentIntegrator.setOrientationLocked(true);
                // Set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);
                // initiate scan
                intentIntegrator.initiateScan();

            }
        });

        scan_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode =2;
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        HomeActivity.this
                );
                // set prompt text
                intentIntegrator.setPrompt("For flash use volume up key");
                // set beep
                intentIntegrator.setBeepEnabled(true);
                // Locked orientation
                intentIntegrator.setOrientationLocked(true);
                // Set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);
                // initiate scan
                intentIntegrator.initiateScan();

            }
        });

        read_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nfc.setUUID(null);
                startActivity(new Intent(getApplicationContext(),Nfc_reader.class));

            }
        });

        //runConnectionThread();

        //startService(new Intent( this, YourService.class ) );
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isGPSenabled()){
                    if(latitude != null && longitude != null){
                        map();
                    }else{
                        showAlert("Localisation Error","L'autorisation d'accéder à la localisation du téléphone n'est pas donnée.");
                    }
                }else{
                    showAlert("Localisation Error","Cette action ne sera pas exécutée tant que vous n'aurez pas activé votre localisation.");
                }


                //sendToServer();


            }

        });
    }


    public void sendToServer(){
        Cursor cursor=DB.getdata();

        try{
            if (cursor.moveToFirst()){

                while (!cursor.isAfterLast()) {
                    final String nfc_qr = cursor.getString(0);
                    final String nfc_uuid = cursor.getString(1);
                    final String nrc_qr = cursor.getString(2);
                    final String audit_id = cursor.getString(3);

                    StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_MAP,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject= new JSONObject(response);
                                        String success= jsonObject.getString("success");
                                        if(success.equals("1")){

                                            Toast.makeText(getApplicationContext(), "Le mapping se fait avec succès", Toast.LENGTH_LONG).show();
                                    /*nfc_qr_tv.setText(null);
                                    nfc_qr_tv.setVisibility(View.VISIBLE);
                                    nfc_uuid_tv.setText(null);
                                    nfc_uuid_tv.setVisibility(View.VISIBLE);
                                    ticket_tv.setText(null);
                                    ticket_tv.setVisibility(View.VISIBLE);*/
                                            /*Nfc.setUUID(null);
                                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
*/
                                        }else if(success.equals("0")) {

                                            showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");

                                        }


                                    }catch (Exception ee){
                                        Toast.makeText(getApplicationContext(), " Error", Toast.LENGTH_LONG).show();
                                    }
                                }


                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    //showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");
                                    Toast.makeText(getApplicationContext(),"Error :"+error.toString(),Toast.LENGTH_LONG).show();

                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("nfc_qr",nfc_qr);
                            params.put("nfc_uuid",nfc_uuid);
                            params.put("ticket_qr",nrc_qr);
                            params.put("id_auditor",audit_id);
                            params.put("latitude",latitude);
                            params.put("longitude",longitude);
                            //params.put("app_version",app_version);
                            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                            System.out.println(latitude+","+longitude);
                            return params;
                        }
                    };
                    RequestQueue requestQueue= Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);

                    //*************End Register*************
                    setResult(RESULT_OK, null);

                }
                    /*String varaible1 = cursor.getString(0);
                    String varaible2 = cursor.getString(1);
                    String varaible3 = cursor.getString(2);
                    String varaible4 = cursor.getString(3);
                    String varaible5 = cursor.getString(4);
                    sqliTest.setText(varaible1+"||"+varaible2+"||"+varaible3+"||"+varaible4+"||"+varaible5);*/

                sqliTest.setText(""+list);

            }
        }finally {
            cursor.close();
        }
    }

    // Thread runnable
    public void runConnectionThread(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
            while (isMyServiceRunning(YourService.class)) {
                try {
                    Thread.sleep(10000);
                    System.out.println("thread running");

                    if (isConnectedToThisServer("https://www.google.com/")) {
                        clicked = 0;
                        System.out.println("connected");
                        connected = true;
                        Log.i("Connection Status","@@@@@@@@ "+connected);
                    } else {
                        System.out.println("not connected");
                        connected = false;
                        Log.i("Connection Status","@@@@@@@@ "+connected);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(clicked == 0){
                                    showDialog();
                                }

                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            }
        });
        thread.start();

        /*new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(hostAvailable("www.google.com", 80)) {
                    connected = true;
                } else {
                    connected = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // update the UI (this is executed on UI thread)
                if(connected){
                    Toast.makeText(HomeActivity.this, "Connected to google", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HomeActivity.this, "Not connected to google", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();*/
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void launchPermission(){
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    public void turnOnGPS(){


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(HomeActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(HomeActivity.this,2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    public void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.no_connection_dialog,null);
        Button btnOk = view.findViewById(R.id.try_again);
        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = 1;
                dialog.dismiss();
            }
        });
    }

    public void saveToSqlite(){
        send.setEnabled(true);
        String nfc_qr = nfc_qr_tv.getText().toString();
        String nfc_uuid = nfc_uuid_tv.getText().toString();
        String ticket_qr = ticket_tv.getText().toString();
        user = sessionManager.getUserDetails();
        String id = user.get(SessionManager.ID);
        Boolean checkInsertData = DB.insertMapData(nfc_qr,nfc_uuid,ticket_qr,id,"false");
        if(checkInsertData == true){
            showDialog();
            /*showAlert("","Les données sont sauvegardées dans le cache avec succés");*/
            Toast.makeText(getApplicationContext(), "Le mapping se fait avec succès", Toast.LENGTH_LONG).show();
            Nfc.setUUID(null);
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }else{
            Toast.makeText(getApplicationContext(),"Already Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    public void map(){
        //System.out.println("Entred");
        send.setEnabled(false);

        if (!validate()) {
            onMapFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this,
                androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_MinWidth);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Mapping en cours...");
        progressDialog.show();

        final String ticket_qr = ticket_tv.getText().toString();
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_CHECK_NRC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            String success= jsonObject.getString("success");
                            if(success.equals("1")){

                                progressDialog.dismiss();
                                send.setEnabled(true);

                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.nrc_error_dialog,null);
                                Button btnOk = view.findViewById(R.id.buttonOk);
                                builder.setView(view);

                                final Dialog dialog = builder.create();
                                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();
                                        Nfc.setUUID(null);
                                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                    }
                                });


                            }else if(success.equals("0")) {

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {

                                                // On complete call either onSignupSuccess or onSignupFailed
                                                // depending on success
                                                onSendSuccess();
                                                progressDialog.dismiss();

                                                // this scope of code for offline functionality
                       /* try {
                            Thread.sleep(10000);
                            System.out.println("thread running");

                            if (isConnectedToThisServer("https://www.google.com/")) {
                                onSendSuccess();
                                progressDialog.dismiss();
                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(HomeActivity.this);
                                        builder1.setMessage("Vue que vous êtes pas connecté, les données seront enregistrés dans le cache. Nous vous informerons dès qu'il seront envoyés au serveur.");
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        saveToSqlite();
                                                    }
                                                });
                                        android.app.AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                        showDialog();


                                    }
                                });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                                            }
                                        }, 3000);

                                // onSignupFailed();

                            }


                        }catch (Exception ee){
                            Toast.makeText(getApplicationContext(), " Error", Toast.LENGTH_LONG).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        send.setEnabled(true);
                        progressDialog.dismiss();
                        //showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");
                        //Toast.makeText(getApplicationContext(),"Error :"+error.toString(),Toast.LENGTH_LONG).show();
                        String find = "TimeoutError";
                        String find2 = "Failed to connect to";
                        boolean val = error.toString().contains(find);
                        boolean val2 = error.toString().contains(find2);
                        if(val) {
                            System.out.println("String found: " + find);
                            showAlert("Erreur de connexion", "Verifier votre connexion et continue");
                        }else if(val2){
                            showAlert("Erreur de serveur", "Le serveur est fermé");

                        }else{
                            System.out.println("string not found");
                        }


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("nrc",ticket_qr);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
        //*************End Register*************
        setResult(RESULT_OK, null);


    }

    public boolean validate() {
        boolean valid = true;

        String qr_nfc = nfc_qr_tv.getText().toString();
        String uuid_nfc = nfc_uuid_tv.getText().toString();
        String qr_ticket = ticket_tv.getText().toString();

        if (qr_nfc.isEmpty()) {

            showAlert("Error","Vous avez pas scanner le code QR de NFC!");
            valid = false;
        }

        if (uuid_nfc.isEmpty()) {

            showAlert("Error","Vous avez pas lire le uuid de NFC!");
            valid = false;
        }

        if(qr_ticket.isEmpty()){

            showAlert("Error","Vous avez pas scanner le code QR de Ticket !");
            valid = false;
        }
        return valid;
    }

    public void showAlert(String title, String message){
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(HomeActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void onMapFailed() {

        Toast.makeText(getBaseContext(), "Échec du Mapping", Toast.LENGTH_LONG).show();
        send.setEnabled(true);

    }

    public void onSendSuccess(){

        send.setEnabled(true);

        final String nfc_qr = nfc_qr_tv.getText().toString();
        final String nfc_uuid = nfc_uuid_tv.getText().toString();
        final String ticket_qr = ticket_tv.getText().toString();
        user = sessionManager.getUserDetails();
        final String id = user.get(SessionManager.ID);

        //if(connected){
            StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_MAP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("#################################"+response);

                            try {
                                JSONObject jsonObject= new JSONObject(response);
                                String success= jsonObject.getString("success");
                                if(success.equals("1")){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.success_dialog,null);
                                    Button btnOk = view.findViewById(R.id.buttonOk);
                                    builder.setView(view);

                                    final Dialog dialog = builder.create();
                                    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.show();

                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog.dismiss();
                                            Nfc.setUUID(null);
                                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                        }
                                    });


                                }else if(success.equals("0")) {

                                    showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");

                                }


                            }catch (Exception ee){

                                Toast.makeText(getApplicationContext(), "error: "+ee, Toast.LENGTH_LONG).show();
                                send.setEnabled(true);

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String find = "TimeoutError";
                            String find2 = "Failed to connect to";
                            boolean val = error.toString().contains(find);
                            boolean val2 = error.toString().contains(find2);
                            if(val) {
                                System.out.println("String found: " + find);
                                showAlert("Erreur de connexion", "Verifier votre connexion et continue");
                            }else if(val2){
                                showAlert("Erreur de serveur", "Le serveur est fermé");


                            }else{
                                System.out.println("string not found");
                            }
                            //
                            //Toast.makeText(getApplicationContext(),"2222 :"+error.toString(),Toast.LENGTH_LONG).show();

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("nfc_qr",nfc_qr);
                    params.put("nfc_uuid",nfc_uuid);
                    params.put("ticket_qr",ticket_qr);
                    params.put("id_auditor",id);
                    params.put("latitude",latitude);
                    params.put("longitude",longitude);
                    params.put("app_version",app_version);
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    System.out.println(latitude+","+longitude);
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            //*************End Register*************
            setResult(RESULT_OK, null);



    }




    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*if(isNetworkConnected()){
            Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    HomeActivity.this
            );
            // set title
            builder.setTitle("Result");
            builder.setMessage("Offline");
            // Set positive button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            // show alert dialog
            builder.show();        }*/
        if(Nfc.getUUID() != null){
            nfc_uuid_tv.setVisibility(View.VISIBLE);
            nfc_uuid_tv.setText(Nfc.getUUID());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Initialize intent result

        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode,resultCode,data
        );
        // check conditions
        //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+intentResult);

        if(intentResult.getContents() != null){

            // when result content is not null
            // Initialize alert dialog
            if(mode==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        HomeActivity.this
                );
                // set title
                builder.setTitle("Result");
                builder.setMessage(intentResult.getContents());
                // Set positive button
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        nfc_qr_tv.setVisibility(View.VISIBLE);
                        nfc_qr_tv.setText(intentResult.getContents());
                    }
                });
                // show alert dialog
                builder.show();

            }if(mode==2){
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        HomeActivity.this
                );
                // set title
                builder.setTitle("Result");
                builder.setMessage(intentResult.getContents().substring(34,58));
                //builder.setMessage(intentResult.getContents().split("=")[1]);
                // Set positive button
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String result= intentResult.getContents();
                        ticket_tv.setVisibility(View.VISIBLE);

                        //ticket_tv.setText(result.substring(result.lastIndexOf("=") + 1));
                        ticket_tv.setText(result.substring(34,58));
                        //ticket_tv.setText(result.split("=")[1]);
                    }
                });
                // show alert dialog
                builder.show();

            }

        }else{
            Toast.makeText(getApplicationContext(),"OOPS...You didn't scan anything", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean isGPSenabled(){
        LocationManager locationManager=null;
        boolean isEnabled = false;
        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sessionManager.logout();
                return(true);
            case R.id.version:
                showAlert("App Version","Vous utilisez la version 1.1 de l'application");
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSenabled()) {

                    LocationServices.getFusedLocationProviderClient(HomeActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(HomeActivity.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        double lat = locationResult.getLocations().get(index).getLatitude();
                                        double longi = locationResult.getLocations().get(index).getLongitude();
                                        latitude = Double.toString(lat);
                                        longitude = Double.toString(longi);
                                        System.out.println("################################ lat:"+latitude);
                                        System.out.println("################################ long:"+longitude);
                                        //ticket_tv.setVisibility(View.VISIBLE);
                                        //ticket_tv.setText(latitude+", "+longitude);


                                    }

                                }

                            }, Looper.getMainLooper());


                } else {
                    turnOnGPS();
                    //showAlert("Location","Activer votre GPS.");
                }
            }
        }

    }


    public boolean isConnectedToThisServer(String host) {
        /*// get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;*/
        /*try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }*/
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            System.out.println(" mExitValue "+exitValue);
            return (exitValue == 0);
        } catch (IOException e){
            System.out.println(" IO Error ");
            e.printStackTrace(); }
        catch (InterruptedException e) {
            System.out.println(" Interrupted Error ");
            e.printStackTrace(); }
        return false;
    }


    public boolean isInternetAvailable(String address, int port, int timeoutMs) {
        try {
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(address, port);

            sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
            sock.close();

            return true;

        } catch (IOException e) { return false; }
    }

    public boolean hostAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            // Either we have a timeout or unreachable host or failed DNS lookup
            System.out.println(e);
            return false;
        }
    }

    private boolean isNetworkConnected() {

        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"not Connected", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    private void unregisterNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){

            e.printStackTrace();

        }
    }

    private void registerNetworkBroadcast(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


}