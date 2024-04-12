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

    private StoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stores);
        // Настройка обработчиков клика для кнопки "Избранное"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Favorite.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Каталог"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Назад"
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
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

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Store store = document.toObject(Store.class);
                        storeList.add(store);
                    }

                    storeAdapter.setStoreList(storeList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e);
                    Toast.makeText(this, "Failed to get data from database", Toast.LENGTH_SHORT).show();
                });
    }

}