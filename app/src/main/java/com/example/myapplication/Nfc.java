package com.example.myapplication;

public class Nfc {
    public static String Numero_Serie=null;
    public static String Technologies=null;
    public static String Type_card=null;
    public static String UUID=null;
    public static Boolean Formate=false;

    public Nfc(){
        Numero_Serie=null;
        Technologies=null;
        Type_card=null;
        UUID=null;
        Formate=false;
    }
    public static String getUUID()
    {
        return UUID;
    }
    public static void setFormate(){
        Formate=true;
    }
    public static Boolean getFormate(){
        return Formate;
    }
    public static void setNumero_Serie(String numero){
        Numero_Serie=numero;
    }

    public static void setTechnologies(String technologies) {
        Technologies = technologies;
    }

    public static void setType_card(String typpe_card) {
        Type_card = typpe_card;
    }

    public static String getNumero_Serie(){
        return Numero_Serie;
    }

    public static String getTechnologies() {
        return Technologies;
    }

    public static String getType_card() {
        return Type_card;
    }


    public static void setUUID(String UUID) {
        Nfc.UUID = UUID;
    }

    public static boolean AllValuesSet(){
        if((Numero_Serie!=null || Technologies!=null || Type_card!=null) && UUID!=null)
            return true;
        else
            return false;
    }
    public static String JsonFormat(){
        String valueJson="{\"Numero_Serie\":\""+Numero_Serie+"\"," +
                "\"Technologies\":\""+Technologies+"\","+"\"UUID\":\""+UUID+"\","+"\"Type_card\":\""+Type_card+"\"}";
        return valueJson;
    }
    public static void putNull(){
        Numero_Serie=null;
        Technologies=null;
        Type_card=null;
        UUID=null;
    }
}
