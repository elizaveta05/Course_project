package com.example.course_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class BouquetsAdapter extends RecyclerView.Adapter<BouquetsAdapter.BouquetsViewHolder> {

    private final Context context;
    private final ArrayList<Bouquets> bouquetsList;

    public BouquetsAdapter(Context context, ArrayList<Bouquets> bouquetsList) {
        this.context = context;
        this.bouquetsList = bouquetsList;
    }

    @NonNull
    @Override
    public BouquetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_cataloge, parent, false);
        return new BouquetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BouquetsViewHolder holder, int position) {
        Bouquets bouquet = bouquetsList.get(position);
        holder.bind(bouquet);
    }

    @Override
    public int getItemCount() {
        return bouquetsList.size();
    }

    public static class BouquetsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productName;
        TextView productPrice;

        public BouquetsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.ProductName);
            productPrice = itemView.findViewById(R.id.ProductPrice);
        }

        public void bind(Bouquets bouquet) {
            Picasso.get().load(bouquet.getImage()).into(imageView);
            productName.setText(bouquet.getName());
            productPrice.setText(String.valueOf(bouquet.getPrice()));
        }
    }
}