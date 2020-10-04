package com.example.mover_f;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adminHistoryAdapter extends RecyclerView.Adapter<adminHistoryViewHolder> {
    private List<adminHistoryObject> itemList;
    private Context context;

    public adminHistoryAdapter(List<adminHistoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }



    @Override
    public adminHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_history_single, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        adminHistoryViewHolder rcv = new adminHistoryViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull adminHistoryViewHolder holder, int position) {
        Log.d("adamxxx", String.valueOf(itemList.get(position)));
        adminHistoryObject model = itemList.get(position);

        holder.driverId.setText(model.getRideId());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}