package com.example.course_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private List<Store> storeList;
    private Context context;

    public StoreAdapter(List<Store> storeList, Context context) {
        this.storeList = storeList;
        this.context = context;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
        notifyDataSetChanged(); // Обновляем адаптер при изменении данных
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_stores, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.streetTextView.setText(store.getAddress());
        holder.telephoneTextView.setText(String.format("Телефон: %s", store.getTelephone()));
        holder.emailTextView.setText(String.format("Почта: %s", store.getEmail()));
        holder.timeTextView.setText(String.format("Время работы: %s", store.getTime()));
    }
    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView streetTextView;
        TextView telephoneTextView;
        TextView emailTextView;
        TextView timeTextView;

        public StoreViewHolder(View view) {
            super(view);
            streetTextView = view.findViewById(R.id.Street);
            telephoneTextView = view.findViewById(R.id.Telephone);
            emailTextView = view.findViewById(R.id.Email);
            timeTextView = view.findViewById(R.id.Time);
        }
    }
}