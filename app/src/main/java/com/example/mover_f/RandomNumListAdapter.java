package com.example.mover_f;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RandomNumListAdapter extends RecyclerView.Adapter<RandomNumListAdapter.myViewHolder> {
    private driver_model driver;
    private List<driver_model> driverList = new ArrayList<>();
    private Context context;

    public RandomNumListAdapter(List<driver_model> driverList, Context context){
        this.driverList = driverList;
        this.context = context;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //yaha par row ko inflate karraoo
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.single_driver,parent,false);
        myViewHolder viewHolder = new myViewHolder(view);


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        driver_model model = driverList.get(position);
        holder.name.setText(model.getName());
        holder.service.setText(model.getService());
        Log.d("ADAM",model.getService());
        holder.car.setText(model.getCar());
        holder.phone.setText(model.getPhone());
        Log.d("adamax", String.valueOf(position));




    }


    @Override
    public int getItemCount() {
        return driverList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView car,name,phone,service;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //img = (ImageView) itemView.findViewById(R.id.driverProfileImageX);
            name = (TextView) itemView.findViewById(R.id.driverNameX);
            phone = (TextView) itemView.findViewById(R.id.driverPhoneX);
            service = (TextView) itemView.findViewById(R.id.driverServiceX);
            car = (TextView) itemView.findViewById(R.id.driverCarX);
        }





    }



    public void update(List<driver_model> datas){
        datas.clear();
        datas.addAll(datas);
    }
}