package com.example.myapplication;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CheckNetwork extends BroadcastReceiver {


    Context mContext;

    public static String isConnected;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(isConnected(context)){
            Toast.makeText(context, "Connected to google", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Not Connected to google", Toast.LENGTH_SHORT).show();
            showDialog();
        }

    }



    public boolean isConnected(Context context){
        boolean is;
        if(isNetworkAvailable(context)){
            is = isConnectedToThisServer();
            return is;
        }else{
            return false;
        }

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println("thread running");
                    if(isNetworkAvailable(context)){
                        if(isConnectedToThisServer()){
                            isConnected = "yes";
                        }else{
                            isConnected = "no";
                        }

                    }else{
                        isConnected = "no network";
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        thread.start();
        return true;*/



    }

    public boolean isConnectedToThisServer(){
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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.no_connection_dialog,null);
        Button btnOk = view.findViewById(R.id.try_again);
        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
