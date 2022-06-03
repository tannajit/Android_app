package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Synchronisation extends AppCompatActivity {

    SessionManager sessionManager;
    DrawerLayout drawerLayout;
    ImageView btnMenu;
    RecyclerView recyclerView, sync_list;
    TextView nbr_mappings;

    static ArrayList<String> arrayList = new ArrayList<>();
    static ArrayList<Integer> iconsList = new ArrayList<>();
    ArrayList<Mapping> mappings = new ArrayList<>();

    MainAdapter mainAdapter;
    SyncAdapter syncAdapter;

    HashMap<String , String> user = null;

    DBHelper DB;
    private TextView user_name;
    private TextView user_email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronisation);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnMenu = (ImageView) findViewById(R.id.btn_menu);
        recyclerView = findViewById(R.id.recycler_view);
        nbr_mappings = (TextView) findViewById(R.id.nbr_mappings);

        DB = new DBHelper(this);
        sync_list = findViewById(R.id.sync_list);
        sync_list.setLayoutManager(new LinearLayoutManager(this));

        //new session
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        //user info;
        user_name = (TextView) findViewById (R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);

        user = sessionManager.getUserDetails();
        String name = user.get(SessionManager.NAME);
        String email = user.get(SessionManager.EMAIL);

        user_name.setText(name);
        user_email.setText(email);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MainAdapter(this,HomeActivity.arrayList,HomeActivity.iconsList,sessionManager));

        mappings.clear();
        /*mappings.add(new Mapping("20AZ45","FGFGSHUYUE54jhjjsddds","AE4567","1","0","01/02/2022"));
        mappings.add(new Mapping("5TRHFG","FGFGSHUYUE54jhjjsddds","AE4567","1","0","01/02/2022"));
        mappings.add(new Mapping("GH657U","FGFGSHUYUE54jhjjsddds","AE4567","1","0","01/02/2022"));
        mappings.add(new Mapping("56YGHJ","FGFGSHUYUE54jhjjsddds","AE4567","1","0","01/02/2022"));*/



        Cursor cursor=DB.getdata();
        try{
            if (cursor.moveToFirst()){

                while (!cursor.isAfterLast()) {
                    final String nfc_qr = cursor.getString(0);
                    final String nfc_uuid = cursor.getString(1);
                    final String nrc_qr = cursor.getString(2);
                    final String audit_id = cursor.getString(3);
                    final String lat = cursor.getString(4);
                    final String lon = cursor.getString(5);
                    final String date= cursor.getString(7);


                    mappings.add(new Mapping(nfc_qr,nfc_uuid,nrc_qr,audit_id,"0",date,lat,lon));
                    cursor.moveToNext();
                }


            }
        }finally {
            cursor.close();
        }
        Collections.reverse(mappings);
        syncAdapter = new SyncAdapter(this,mappings);
        sync_list.setAdapter(syncAdapter);

        nbr_mappings.setText(String.valueOf(mappings.size()));


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        HomeActivity.closeDrawer(drawerLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        syncAdapter.notifyDataSetChanged();

        Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show();
        nbr_mappings.setText(String.valueOf(syncAdapter.getItemCount()));
    }
}