package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Shop extends AppCompatActivity implements AdapterOrder.OrderListener {
    // Инициализация переменных
    private RecyclerView recyclerView;
    private AdapterOrder adapter;
    private ArrayList<Bouquets> bouquetsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private  FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView tv_cost;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_shop);

          // Инициализация Firebase Authentication и Firestore
          mAuth = FirebaseAuth.getInstance();
          db = FirebaseFirestore.getInstance();

          // Настройка обработчиков клика для кнопок
          setupClickListeners();

          setupRecyclerView();

          tv_cost = findViewById(R.id.tv_cost);

          // Получение общей стоимости и установка в TextView
          adapter.getCost(totalCost -> tv_cost.setText(String.format("%s ₽", totalCost)));

      }
    // Настройка обработчиков клика для кнопок
    private void setupClickListeners() {
        // Устанавливаем обработчики кликов для кнопок навигации
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));
        // Настройка обработчика клика для кнопки "Каталог"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        // Настройка обработчика клика для кнопки "Избранное"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(this, Favorite.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Убрать анимацию перехода

            } else {
                Intent intent = new Intent(this, activity_account.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Убрать анимацию перехода
                Toast.makeText(this, "Войдите в аккаунт для добавления в избранное", Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка обработчика клика для кнопки "Аккаунт"
        ImageButton btn_account = findViewById(R.id.btn_account);
        btn_account.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(this, PersonalAccount.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                Intent intent = new Intent(this, activity_account.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        // Настройка обработчика клика для кнопки "Очистить корзину"
        ImageView btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("ListOrder")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String orderId = documentSnapshot.getId();
                            db.collection("ListOrder").document(orderId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        adapter.fetchBouquetQuantitiesByUser(); // Обновление списка избранных букетов
                                        tv_cost.setText("0");
                                        adapter.notifyDataSetChanged();
                                        //Toast.makeText(this, "Корзина успешно очищена", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Ошибка при очистке корзины", Toast.LENGTH_SHORT).show();
                                        Log.e("ORDER", "Error deleting order document: " + e.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ORDER", "Error getting order bouquets for user", e));
        });
    }

    // Метод для запуска новой активности
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.ll_content);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AdapterOrder(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        // Устанавливаем список избранных букетов в адаптер и обновляем RecyclerView
        ArrayList<Bouquets> bouquetsList = adapter.getfetchBouquetQuantitiesByUser();
        adapter.setfetchBouquetQuantitiesByUser(bouquetsList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Bouquets bouquet) {

    }

    @Override
    public void onCostReceived(int totalCost) {
        runOnUiThread(() -> tv_cost.setText(String.format("%s ₽", totalCost)));
    }
}


