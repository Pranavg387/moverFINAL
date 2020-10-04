package com.example.mover_f;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adminHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView driverId;

    public adminHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        driverId = itemView.findViewById(R.id.driverIdX);

    }


    @Override
    public void onClick(View v) {

        Log.d("ADAMX99",driverId.getText().toString());
        Intent intent = new Intent(v.getContext(), adminDetailedHistory.class);
        Bundle b = new Bundle();
        b.putString("driverId", driverId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }
}
