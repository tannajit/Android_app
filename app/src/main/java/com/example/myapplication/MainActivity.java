package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "prefs";
    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager=new SessionManager(this);
        new BackgroundTask().execute();
    }

    private class BackgroundTask extends AsyncTask {
        Intent intent1,intent2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            intent1 = new Intent(MainActivity.this, LoginActivity.class);
            intent2 = new Intent(MainActivity.this, HomeActivity.class);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            /*  Use this method to load background
             * data that your app needs. */

            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            Pass your loaded data here using Intent
            sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            //new session

            if(sessionManager.isLoggin()==true){
                startActivity(intent2);
                finish();
            }else{
                startActivity(intent1);
                finish();
            }
//

        }
    }
}