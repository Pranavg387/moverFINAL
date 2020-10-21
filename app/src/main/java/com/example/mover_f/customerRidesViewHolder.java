package com.example.mover_f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class customerRidesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView to;
    TextView from;
    private String driverFoundID = null;


    public customerRidesViewHolder(@NonNull View itemView) {
        super(itemView);
        to = itemView.findViewById(R.id.rideTo);
        from = itemView.findViewById(R.id.rideFrom);
        Log.d("AdamXXx", (String) to.getText());

    }

    @Override
    public void onClick(View v) {
        Log.d("AdamXXx", (String) to.getText());
        Intent intent = new Intent(v.getContext(), customerMapsActivity.class);
        intent.putExtra("driverFoundId", driverFoundID);
        v.getContext().startActivity(intent);

    }
}
