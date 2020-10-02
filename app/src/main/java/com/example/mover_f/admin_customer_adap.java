package com.example.mover_f;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import historyRecyclerView.HistoryObject;
import historyRecyclerView.HistoryViewHolders;

public class admin_customer_adap extends RecyclerView.Adapter<admin_customer_ViewHolder> {

    private List<admin_customer_model> itemList;
    private Context context;

    public admin_customer_adap(List<admin_customer_model> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }



    @Override
    public admin_customer_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_customer_single, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        admin_customer_ViewHolder rcv = new admin_customer_ViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull admin_customer_ViewHolder holder, int position) {
        admin_customer_model model = itemList.get(position);
        holder.name.setText(model.getName());
        holder.phone.setText(model.getPhone());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


