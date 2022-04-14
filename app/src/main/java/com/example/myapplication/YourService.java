package com.example.myapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class YourService extends Service {
    public static boolean connected;
    Thread thread;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("thread running");
                    if(hostAvailable("https://www.google.com/",80)) {
                        System.out.println("connected");
                    } else {
                        System.out.println("not connected");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/
        Log.i("SERVICE","########### Service Started #######");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(thread != null){
            thread.interrupt();
        }
    }
}
