package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "sales.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Map(nfc_qr TEXT,nfc_uuid TEXT,ticket_qr TEXT primary key, id_auditor TEXT,synchronized TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Map");
    }

    public boolean insertMapData(String nfc_qr,String nfc_uuid, String ticket_qr, String id_audit,String sync){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nfc_qr",nfc_qr);
        contentValues.put("nfc_uuid",nfc_uuid);
        contentValues.put("ticket_qr",ticket_qr);
        contentValues.put("id_auditor",id_audit);
        contentValues.put("synchronized",sync);
        long result = DB.insert("Map",null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }



    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Map", null);
        return cursor;
    }
}
