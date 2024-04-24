package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class List_of_stores extends AppCompatActivity {

    private StoreAdapter storeAdapter; // Адаптер для списка магазинов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stores);

        // Настройка обработчиков клика для различных кнопок навигации
        setNavigationButtonsClickListeners();

        // Инициализация RecyclerView
        RecyclerView recyclerView = findViewById(R.id.store_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        storeAdapter = new StoreAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(storeAdapter);

        // Получение данных из базы данных Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Получение коллекции "Store" из Firestore и добавление слушателя
        db.collection("Store").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Store> storeList = new ArrayList<>();

                    // Обход результатов запроса и добавление магазинов в список
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Store store = document.toObject(Store.class);
                        storeList.add(store);
                    }

                    storeAdapter.setStoreList(storeList); // Установка списка магазинов в адаптер
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e);
                    Toast.makeText(this, "Failed to get data from database", Toast.LENGTH_SHORT).show();
                });
    }

    // Установка обработчиков кликов для кнопок навигации
    private void setNavigationButtonsClickListeners() {
        setNavigationButtonClickListener(R.id.btn_favorites, Favorite.class);
        setNavigationButtonClickListener(R.id.btn_shop, ShoppingCart.class);
        setNavigationButtonClickListener(R.id.btn_cataloge, Category.class);
        setNavigationButtonClickListener(R.id.btn_main, MainActivity.class);
        setNavigationButtonClickListener(R.id.btn_account, MainActivity.class);
    }

    // Установка обработчика клика на кнопку навигации
    private void setNavigationButtonClickListener(int buttonId, Class<?> cls) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, cls);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });
    }
}