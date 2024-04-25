package com.example.course_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Adapter_list_order extends RecyclerView.Adapter<Adapter_list_order.OrderViewHolder> {
    private Context context;
    private List<Bouquets> bouquetsList;
    private ArrayList<Bouquets> bouquetQuantities = new ArrayList<>();
    private static FirebaseFirestore db;
    private OrderListener listener;

    public Adapter_list_order(Context context, List<Bouquets> bouquetsList, OrderListener listener) {
        this.context = context;
        this.bouquetsList = bouquetsList;
        this.db = FirebaseFirestore.getInstance();
        this.listener=listener;

    }

    public void setfetchBouquetQuantitiesByUser(ArrayList<Bouquets> bouquets) {
        bouquetsList = bouquets;
    }
    public ArrayList<Bouquets> getfetchBouquetQuantitiesByUser() {
        fetchBouquetQuantitiesByUser();
        return bouquetQuantities;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_list_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bouquets currentBouquet = bouquetsList.get(position);

        // Проверяем, есть ли уже данный вид букета в списке
        int totalQuantity = 0;
        for (Bouquets bouquet : bouquetsList) {
            if (bouquet.getId().equals(currentBouquet.getId())) {
                totalQuantity += bouquet.getQuantity();
            }
        }

        if (totalQuantity > 0) {
            // Отображаем только один раз с учетом общего количества и стоимости
            holder.bind(currentBouquet, totalQuantity);
        }

    }

    @Override
    public int getItemCount() {
        // Возвращаем общее количество уникальных букетов
        return bouquetsList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView bouquetName;
        private TextView bouquetCost;
        private TextView bouquetQuantity;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_bouquet_image);
            bouquetName = itemView.findViewById(R.id.tv_bouquet_name);
            bouquetCost = itemView.findViewById(R.id.tv_bouquet_cost);
            bouquetQuantity = itemView.findViewById(R.id.tv_quantity);
        }

        public void bind(Bouquets bouquet, int totalQuantity) {
            Picasso.get().load(bouquet.getImage()).into(imageView);
            bouquetName.setText(bouquet.getName());
            int totalCost = Integer.parseInt(bouquet.getCost()) * totalQuantity;
            bouquetCost.setText(String.format("%s ₽", totalCost));
            bouquetQuantity.setText(String.format("%s шт.", totalQuantity));

        }
    }
    public interface OrderListener {
        void onClick(Bouquets bouquet);

        void onCostReceived(int totalCost);
    }
    public void getCost(CostListener_shop_cart listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AtomicInteger total = new AtomicInteger();
        AtomicInteger loadedBouquets = new AtomicInteger();

        db.collection("ListOrder")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalBouquets = queryDocumentSnapshots.size();
                    if (totalBouquets == 0) {
                        listener.onCostReceived(0);
                    }

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String bouquetId = documentSnapshot.getString("bouquetId");

                        if (bouquetId != null) {
                            db.collection("Bouquets")
                                    .document(bouquetId)
                                    .get()
                                    .addOnSuccessListener(bouquetDocument -> {
                                        if (bouquetDocument.exists()) {
                                            Bouquets orderBouquet = bouquetDocument.toObject(Bouquets.class);
                                            orderBouquet.setId(bouquetDocument.getId());
                                            if (orderBouquet != null) {
                                                total.addAndGet(Integer.parseInt(orderBouquet.getCost()));
                                            }
                                        }

                                        loadedBouquets.incrementAndGet();

                                        if (loadedBouquets.get() == totalBouquets) {
                                            listener.onCostReceived(total.get());
                                        }

                                    })
                                    .addOnFailureListener(e -> Log.e("ORDER", "Error getting bouquet document", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ORDER", "Error getting order bouquets for user", e));
    }
        public void fetchBouquetQuantitiesByUser() {
        bouquetQuantities.clear(); // Очищаем список избранных букетов

        // Получаем идентификатор текущего пользователя
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Получаем данные из коллекции ListOrder в Firestore для данного пользователя
        db.collection("ListOrder")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Создаем карту для хранения и подсчета количества каждого вида букета
                    Map<String, Integer> bouquetQuantityMap = new HashMap<>();

                    // Проходим по каждому документу с данными о букетах
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String bouquetId = documentSnapshot.getString("bouquetId");
                        if (bouquetId != null) {
                            if (bouquetQuantityMap.containsKey(bouquetId)) {
                                int currentQuantity = bouquetQuantityMap.get(bouquetId);
                                bouquetQuantityMap.put(bouquetId, currentQuantity + 1);
                            } else {
                                bouquetQuantityMap.put(bouquetId, 1);
                            }
                        }
                    }

                    // Проходим по карте с количеством букетов и извлекаем данные о каждом виде букета
                    for (Map.Entry<String, Integer> entry : bouquetQuantityMap.entrySet()) {
                        String bouquetId = entry.getKey();
                        int quantity = entry.getValue();

                        // Получаем информацию о букете из коллекции Bouquets в Firestore
                        db.collection("Bouquets")
                                .document(bouquetId)
                                .get()
                                .addOnSuccessListener(bouquetDocument -> {
                                    if (bouquetDocument.exists()) {
                                        // Преобразуем данные о букете в объект класса Bouquets
                                        Bouquets orderBouquet = bouquetDocument.toObject(Bouquets.class);
                                        orderBouquet.setId(bouquetDocument.getId());
                                        orderBouquet.setQuantity(quantity);

                                        // Проверяем, если букет уже есть в списке, обновляем его количество
                                        boolean found = false;
                                        for (Bouquets bouquet : bouquetQuantities) {
                                            if (bouquet.getId().equals(orderBouquet.getId())) {
                                                found = true;
                                                bouquet.setQuantity(quantity);
                                                break;
                                            }
                                        }

                                        // Если букета еще нет в списке, добавляем его
                                        if (!found) {
                                            bouquetQuantities.add(orderBouquet);
                                        }

                                        // Уведомляем об успешном обновлении данных
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("ORDER", "Error getting order bouquets for user", e));
    }
}