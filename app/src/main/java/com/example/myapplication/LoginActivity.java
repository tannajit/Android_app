package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    SessionManager sessionManager;

    HashMap<String, String> user = null;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    boolean vue;


    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "password";
    private static final String KEY_REMEMBER = "remember";


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        _emailText = (EditText)findViewById(R.id.email) ;
        _passwordText = (EditText) findViewById(R.id.password);
        _loginButton = (Button)findViewById(R.id.login);


        _passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (_passwordText.getRight() - _passwordText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if(!vue){
                            _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            _passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_view,0);
                            vue=true;
                        }else if(vue==true){
                            _passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            _passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_view_off,0);
                            vue=false;
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_MinWidth);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();


        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            String success= jsonObject.getString("success");
                            JSONArray jsonArray= jsonObject.getJSONArray("login");
                            if(success.equals("1")){
                                for(int i=0 ; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    String id=object.getString("id").trim();
                                    Log.d(TAG, id);
                                    String name=object.getString("name").trim();
                                    Log.d(TAG, name);
                                    String email=object.getString("email").trim();
                                    Log.d(TAG, email);
                                    String pass=object.getString("password").trim();
                                    Log.d(TAG, pass);
                                    String changed_pass = object.getString("pass_changed").trim();
                                    if(changed_pass.equals("1")){
                                        sessionManager.createSession(id,name,email,pass);
                                        Toast.makeText(getApplicationContext()," Bienvenue",Toast.LENGTH_LONG).show();
                                        Intent intent= new Intent(LoginActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                    }else if(changed_pass.equals("0")){
                                        // request password change
                                        // get password change dialog view
                                        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                                        View passView = li.inflate(R.layout.change_pass_dialog, null);

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                LoginActivity.this);
                                        // set prompts.xml to alertdialog builder
                                        alertDialogBuilder.setView(passView);

                                        final EditText userPass = (EditText) passView
                                                .findViewById(R.id.edited_password);
                                        // hide show pass
                                        userPass.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                final int DRAWABLE_LEFT = 0;
                                                final int DRAWABLE_TOP = 1;
                                                final int DRAWABLE_RIGHT = 2;
                                                final int DRAWABLE_BOTTOM = 3;

                                                if(event.getAction() == MotionEvent.ACTION_UP) {
                                                    if(event.getRawX() >= (userPass.getRight() - userPass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                                                        if(!vue){
                                                            userPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                                            userPass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_view,0);
                                                            vue=true;
                                                        }else if(vue==true){
                                                            userPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                            userPass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_view_off,0);
                                                            vue=false;
                                                        }

                                                        return true;
                                                    }
                                                }
                                                return false;
                                            }
                                        });

                                        final EditText confirmPass = (EditText) passView
                                                .findViewById(R.id.confirm_password);
                                        final Button ok = (Button) passView
                                                .findViewById(R.id.done);


                                        // validate pass

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (!(confirmPass.getText().toString().trim().equals(userPass.getText().toString().trim()))) {
                                                    confirmPass.setError("Password Do not match");

                                                } else if(confirmPass.getText().toString().trim().equals(userPass.getText().toString().trim())){
                                                    confirmPass.setError(null);
                                                    updatePass(userPass.getText().toString().trim(),email);
                                                }


                                            }
                                        });


                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // show it
                                        alertDialog.show();
                                    }





                                }

                            }else if(success.equals("0")){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setTitle("We Couldn't Find An Account");
                                builder1.setMessage(" Your email or password are incorrect! ");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setTitle("We Couldn't Find An Account");
                            builder1.setMessage("The credentials you entered doesn't match an account, you can enter a different ones, or create a new account.");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Create Account",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                            //Toast.makeText(getApplicationContext(),"Error :"+e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error :"+error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void updatePass(String pass, String email){



        StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_UPDATE_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            String success= jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(getApplicationContext(), "Mot de passe a été changé avec succés", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else if(success.equals("0")) {
                                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(LoginActivity.this);
                                builder1.setTitle("Server Error");
                                builder1.setMessage("There is an error in the server, please try again later.");
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


                        }catch (Exception ee){
                            Toast.makeText(getApplicationContext(), " Error", Toast.LENGTH_LONG).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error :"+error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("password",pass);
                params.put("email",email);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}