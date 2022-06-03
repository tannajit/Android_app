package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.ViewHolder> {

    Activity activity;
    ArrayList<Mapping> syncData;
    SyncAdapter adapter;
    public static String app_version = "1.2";
    DBHelper DB;

    public SyncAdapter(Activity activity, ArrayList<Mapping> arrayList){
        this.activity = activity;
        this.syncData = arrayList;
        this.adapter = this;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.sync_item_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Mapping mappingDataList = syncData.get(position);
        holder.nfc_qr.setText(mappingDataList.getNfc_qr());
        holder.add_date.setText(mappingDataList.getDate_created());

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.send.setEnabled(false);
                final String nfc_qr = mappingDataList.getNfc_qr();
                final String nfc_uuid = mappingDataList.getNfc_uuid();
                final String nrc_qr = mappingDataList.getNrc_qr();
                final String audit_id = mappingDataList.getAudit_id();
                final String latitude = mappingDataList.getLatitude();
                final String longitude = mappingDataList.getLongitude();

                final ProgressDialog progressDialog = new ProgressDialog(activity,
                        androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_MinWidth);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mapping en cours...");
                progressDialog.show();




                    StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_CHECK_NRC,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject= new JSONObject(response);
                                        String success= jsonObject.getString("success");
                                        if(success.equals("1")){

                                            progressDialog.dismiss();
                                            holder.send.setEnabled(true);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view = inflater.inflate(R.layout.nrc_error_dialog,null);
                                            Button btnOk = view.findViewById(R.id.buttonOk);
                                            builder.setView(view);

                                            final Dialog dialog = builder.create();
                                            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.show();

                                            btnOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    DB = new DBHelper(activity);
                                                    DB.deleteMapping(nrc_qr);
                                                    syncData.remove(mappingDataList);
                                                    syncData.addAll(syncData);
                                                    adapter.notifyDataSetChanged();
                                                    dialog.dismiss();
                                                    activity.finish();
                                                    activity.startActivity(activity.getIntent());

                                                    //showAlert("Super!!","Cette mapping a été supprimée du cache");

                                                }
                                            });


                                        }else if(success.equals("0")) {

                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {

                                                            // On complete call either onSignupSuccess or onSignupFailed
                                                            // depending on success
                                                            StringRequest stringRequest= new StringRequest(Request.Method.POST, URLs.URL_MAP,
                                                                    new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            System.out.println("#################################"+response);

                                                                            try {
                                                                                JSONObject jsonObject= new JSONObject(response);
                                                                                String success= jsonObject.getString("success");
                                                                                if(success.equals("1")){

                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                                                                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                                    View view = inflater.inflate(R.layout.success_sync_offline,null);
                                                                                    Button btnOk = view.findViewById(R.id.buttonOk);
                                                                                    builder.setView(view);

                                                                                    final Dialog dialog = builder.create();
                                                                                    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    dialog.show();

                                                                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {


                                                                                            DB = new DBHelper(activity);
                                                                                            DB.deleteMapping(nrc_qr);
                                                                                            syncData.remove(mappingDataList);
                                                                                            syncData.addAll(syncData);
                                                                                            adapter.notifyDataSetChanged();
                                                                                            dialog.dismiss();
                                                                                            activity.finish();
                                                                                            activity.startActivity(activity.getIntent());

                                                                                            //showAlert("Mapping est supprimée","Cette mapping a été supprimée du cache");

                                                                                        }
                                                                                    });


                                                                                }else if(success.equals("0")) {

                                                                                    showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");

                                                                                }


                                                                            }catch (Exception ee){

                                                                                Toast.makeText(activity, "error: "+ee, Toast.LENGTH_LONG).show();
                                                                                holder.send.setEnabled(true);

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

                                                                                showAlert("Erreur de connexion", "Verifier votre connexion et continue");

                                                                            }else if(val2){
                                                                                showAlert("Erreur", "Vérifier votre connexion et continue");
                                                                            }else{
                                                                                System.out.println("string not found");
                                                                            }

                                                                            //Toast.makeText(getApplicationContext(),"2222 :"+error.toString(),Toast.LENGTH_LONG).show();

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
                                                                    params.put("app_version",app_version);
                                                                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                                                                    System.out.println(latitude+","+longitude);
                                                                    return params;
                                                                }
                                                            };
                                                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                            RequestQueue requestQueue= Volley.newRequestQueue(activity);
                                                            requestQueue.add(stringRequest);
                                                            //*************End Register*************
                                                            activity.setResult(RESULT_OK, null);


                                                            progressDialog.dismiss();

                                                            // this scope of code for offline functionality

                                                        }
                                                    }, 3000);

                                            // onSignupFailed();

                                        }


                                    }catch (Exception ee){
                                        Toast.makeText(activity, " Error", Toast.LENGTH_LONG).show();
                                    }
                                }


                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    holder.send.setEnabled(true);
                                    progressDialog.dismiss();
                                    //showAlert("Erreur de serveur","Il y a une erreur dans le serveur, veuillez réessayer plus tard.");
                                    //Toast.makeText(getApplicationContext(),"Error :"+error.toString(),Toast.LENGTH_LONG).show();
                                    String find = "TimeoutError";
                                    String find2 = "Failed to connect to";
                                    boolean val = error.toString().contains(find);
                                    boolean val2 = error.toString().contains(find2);
                                    if(val) {
                                        showAlert("Erreur de connexion", "Vérifier votre connexion et réssayer");
                                        //showAlert("Erreur de connexion", "Verifier votre connexion et continue");
                                    }else if(val2){
                                        showAlert("Erreur", "Vérifier votre connexion et réssayer");

                                    }else{
                                        System.out.println("string not found");
                                    }


                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("nrc",nrc_qr);
                            return params;
                        }
                    };
                    RequestQueue requestQueue= Volley.newRequestQueue(activity);
                    requestQueue.add(stringRequest);
                    //*************End Register*************
                    activity.setResult(RESULT_OK, null);
                    // ################## end code when connected

            }
        });

    }




    public void showAlert(String title, String message){
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(activity);
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

    @Override
    public int getItemCount() {
        return syncData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView nfc_qr, add_date;
        Button send;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nfc_qr = itemView.findViewById(R.id.sync_nfc_qr);
            add_date = itemView.findViewById(R.id.sync_date);
            send = itemView.findViewById(R.id.send_btn);

        }
    }
}
