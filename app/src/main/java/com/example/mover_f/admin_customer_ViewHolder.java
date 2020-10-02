package com.example.mover_f;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class admin_customer_ViewHolder extends RecyclerView.ViewHolder {
   TextView name;
    TextView phone;

    public admin_customer_ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.customerNameX);
        phone = itemView.findViewById(R.id.customerPhoneX);

    }
}