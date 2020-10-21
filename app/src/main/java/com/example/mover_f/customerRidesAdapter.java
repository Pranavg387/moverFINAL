package com.example.mover_f;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class customerRidesAdapter extends RecyclerView.Adapter<customerRidesViewHolder> {
    private List<customer_rides_model> itemList;
    private Context context;

    public customerRidesAdapter(List<customer_rides_model> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public customerRidesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_customer_ride, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        customerRidesViewHolder rcv = new customerRidesViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull customerRidesViewHolder holder, int position) {
        customer_rides_model model = itemList.get(position);
        holder.to.setText(model.getDestinaion());
        holder.from.setText(model.getPickup());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
