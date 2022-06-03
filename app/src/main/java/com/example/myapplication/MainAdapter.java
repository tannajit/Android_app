package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    SessionManager sessionManager;
    Activity activity;
    ArrayList<String> arrayList;
    ArrayList<Integer> iconsList;

    DBHelper DB;



    public MainAdapter(Activity activity, ArrayList<String> arrayList, ArrayList<Integer> arrayList1,SessionManager sessionManager){
        this.sessionManager = sessionManager;
        this.sessionManager.checkLogin();
        this.activity = activity;
        this.arrayList = arrayList;
        this.iconsList = arrayList1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(arrayList.get(position));
        holder.imageView.setImageResource(iconsList.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                switch (position){
                    case 0:
                        activity.startActivity(new Intent(activity,HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity,Synchronisation.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case 2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("App Version");
                        builder.setMessage("Vous utilisez la version 1.1 de l'application");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;
                    case 3:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                        builder2.setTitle("Déconnecter");
                        builder2.setMessage("Vous êtes sûr de vouloir déconnecter ?");
                        builder2.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // other warning popup
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(activity);
                                builder3.setTitle("Attention");
                                builder3.setMessage("Vos données de synchronisation seront supprimées!");
                                builder3.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DB = new DBHelper(activity);
                                        DB.clearTable();
                                        sessionManager.logout();
                                    }
                                });
                                builder3.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder3.show();


                            }
                        });

                        builder2.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder2.show();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_v);
            imageView = itemView.findViewById(R.id.item_icon);

        }
    }
}
