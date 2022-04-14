package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE=0;

    public static final String PREF_NAME="LOGIN";
    public static final String LOGIN="IS_LOGIN";
    public static final String ID="ID";
    public static final String NAME="NAME";
    public static final String EMAIL="EMAIL";
    public static final String PASSWORD="PASSWORD";


    public SessionManager(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public void createSession(String id, String name,String email ,String password ){
        editor.putBoolean(LOGIN,true);
        editor.putString(ID,id);
        editor.putString(NAME,name);
        editor.putString(EMAIL,email);
        editor.putString(PASSWORD,password);
        editor.apply();
    }


    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }
    public void checkLogin(){
        if(!this.isLoggin()){
            Intent i= new Intent(context,MainActivity.class);
            context.startActivity(i);
            ((HomeActivity)context).finish();
        }
    }
    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user= new HashMap<>();
        user.put(ID,sharedPreferences.getString(ID,null));
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(EMAIL,sharedPreferences.getString(EMAIL,null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i= new Intent(context,LoginActivity.class);
        context.startActivity(i);
        ((HomeActivity)context).finish();
    }


}
