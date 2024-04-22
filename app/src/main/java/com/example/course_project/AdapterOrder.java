package com.example.course_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.OrderViewHolder> {
    private Context context;
    private ArrayList<Bouquets> bouquetsList;
    private HashMap<String, Integer> bouquetQuantities;

    public AdapterOrder(Context context, ArrayList<Bouquets> bouquetsList) {
        this.context = context;
        this.bouquetsList = bouquetsList;
    }
    public void setBouquetsList(ArrayList<Bouquets> bouquets) {
        bouquetsList = bouquets;
    }
    public void setBouquetQuantities(HashMap<String, Integer> bouquetQuantities) {
        this.bouquetQuantities = bouquetQuantities;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_douquets_in_basket, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bouquets bouquet = bouquetsList.get(position);
        int quantity = bouquetQuantities.get(bouquet.getId()); // Получаем количество для данного букета
        holder.bind(bouquet, quantity);
    }
    @Override
    public int getItemCount() {
        return bouquetsList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView bouquetName;
        TextView bouquetCost;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            bouquetName = itemView.findViewById(R.id.tv_bouquet_name);
            bouquetCost = itemView.findViewById(R.id.tv_bouquet_cost);
        }

        public void bind(Bouquets bouquet, int quantity) {
            bouquetName.setText(bouquet.getName());
            bouquetCost.setText("Cost: " + bouquet.getCost() + ", Quantity: " + quantity);
        }
    }
}