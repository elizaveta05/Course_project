package com.example.course_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.OrderViewHolder> {
    private Context context;
    private ArrayList<Bouquets> bouquetsList;
    private ArrayList<Bouquets> bouquetQuantities = new ArrayList<>();
    private static FirebaseFirestore db;
    private OrderListener listener;


    public AdapterOrder(Context context, ArrayList<Bouquets> bouquetsList, OrderListener listener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_douquets_in_basket, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bouquets bouquet = bouquetsList.get(position);

        holder.bind(bouquet);

        holder.btnInformation.setOnClickListener(v -> {
            Toast.makeText(context, "Информация о стоимости букета '" + bouquet.getName() + "' " + bouquet.getCost() + "₽!", Toast.LENGTH_SHORT).show();
        });

        holder.btnAdd.setOnClickListener(v -> {
            // Получаем идентификатор пользователя
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (userId != null) {
                // Получаем идентификатор букета
                String bouquetId = bouquet.getId();
                if (bouquetId != null) {
                    OrderCart order = new OrderCart(userId, bouquetId);
                    if (order != null) {
                        // Добавляем букет в коллекцию ListOrder в Firestore
                        db.collection("ListOrder")
                                .add(order)
                                .addOnSuccessListener(documentReference -> {
                                    fetchBouquetQuantitiesByUser(); // Обновление списка избранных букетов
                                    getCost(totalCost -> {
                                        // Обновляем общую стоимость после получения всех данных
                                        listener.onCostReceived(totalCost);
                                    });
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Количество букетов увеличено!", Toast.LENGTH_SHORT).show();
                                    Log.d("ListOrder", "Bouquet added to ListOrder successfully!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ListOrder", "Error adding bouquet to ListOrder", e);
                                });
                    } else {
                        Toast.makeText(context, "Ошибка! Проблемы с созданием объекта", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Ошибка! Букет не найден", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Ошибка! Пользователь не найден", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnReduce.setOnClickListener(v -> {
            // Получаем идентификатор пользователя
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (userId != null) {
                // Получаем идентификатор букета
                String bouquetId = bouquet.getId();
                if (bouquetId != null) {
                    // Поиск документа соответствующего идентификаторам пользователя и букета
                    db.collection("ListOrder")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("bouquetId", bouquetId)
                            .limit(1) // Указываем limit(1), чтобы удалить только один документ
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Удаляем найденный документ из коллекции ListOrder
                                    String docId = documentSnapshot.getId();
                                    db.collection("ListOrder").document(docId).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                fetchBouquetQuantitiesByUser(); // Обновление списка избранных букетов
                                                getCost(totalCost -> {
                                                    // Обновляем общую стоимость после получения всех данных
                                                    listener.onCostReceived(totalCost);
                                                });
                                                notifyDataSetChanged(); // Обновление разметки
                                                Toast.makeText(context, "Букет удален из корзины", Toast.LENGTH_SHORT).show();

                                                Log.d("ListOrder", "Bouquet removed from ListOrder successfully!");
                                            })
                                            .addOnFailureListener(e -> {
                                                // Обработка ошибки
                                                Log.e("ListOrder", "Error removing bouquet from ListOrder", e);
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Обработка ошибки при получении документов
                                Log.e("ListOrder", "Error getting documents", e);
                            });
                } else {
                    Toast.makeText(context, "Ошибка! Букет не найден", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Ошибка! Пользователь не найден", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btn_delete_bouquet.setOnClickListener(v -> {
            // Получаем идентификатор пользователя
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String bouquetId = bouquet.getId();

            if (userId != null && bouquetId != null) {
                db.collection("ListOrder")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("bouquetId", bouquetId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String docId = documentSnapshot.getId();
                                db.collection("ListOrder").document(docId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            fetchBouquetQuantitiesByUser(); // Обновление списка избранных букетов
                                            getCost(totalCost -> {
                                                // Обновляем общую стоимость после получения всех данных
                                                listener.onCostReceived(totalCost);
                                            });
                                            notifyDataSetChanged(); // Обновление разметки
                                            Log.d("ListOrder", "Bouquet removed from ListOrder successfully!");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ListOrder", "Error removing bouquet from ListOrder", e);
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ListOrder", "Error getting documents for bouquet deletion", e);
                        });
            } else {
                Toast.makeText(context, "Ошибка при удалении букетов из корзины", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return bouquetsList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView bouquetName;
        TextView bouquetCost;
        TextView bouquetQuantity;
        ImageView btnInformation;
        ImageButton btnAdd;

        ImageButton btnReduce;
        ImageButton btn_delete_bouquet;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_bouquet_image);
            bouquetName = itemView.findViewById(R.id.tv_bouquet_name);
            bouquetCost = itemView.findViewById(R.id.tv_bouquet_cost);
            bouquetQuantity = itemView.findViewById(R.id.tv_quantity);
            btnInformation = itemView.findViewById(R.id.btn_info);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnReduce = itemView.findViewById(R.id.btn_reduce);
            btn_delete_bouquet = itemView.findViewById(R.id.btn_delete_bouquet);


        }

        public void bind(Bouquets bouquet) {
            Picasso.get().load(bouquet.getImage()).into(imageView);
            bouquetName.setText(bouquet.getName());
            int totalCost = Integer.parseInt(bouquet.getCost()) * bouquet.getQuantity();
            bouquetCost.setText(String.format("%s ₽", totalCost));
            bouquetQuantity.setText(String.format("%s шт.", bouquet.getQuantity()));
        }
    }
    public interface OrderListener {
        void onClick(Bouquets bouquet);

        void onCostReceived(int totalCost);
    }
    public void getCost(CostListener listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AtomicInteger total = new AtomicInteger();
        AtomicInteger processedCount = new AtomicInteger(0);

        db.collection("ListOrder")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalDocs = queryDocumentSnapshots.size();

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

                                        processedCount.incrementAndGet();

                                        if (processedCount.get() == totalDocs) {
                                            listener.onCostReceived(total.get());
                                        }
                                    });
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