package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity implements BouquetsAdapter.FavoritesListener {
    private ArrayList<Bouquets> favoriteBouquets = new ArrayList<>(); // Создание списка избранных букетов
    private BouquetsAdapter adapter; // Объявление объекта адаптера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Настройка RecyclerView для отображения избранных букетов
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация списка избранных букетов и адаптера
        favoriteBouquets = new ArrayList<>();
        adapter = new BouquetsAdapter(this, favoriteBouquets, this);

        // Привязка адаптера к RecyclerView
        recyclerView.setAdapter(adapter);

        // Настройка обработчиков клика для кнопок
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
        });

        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
        });

        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onFavoriteClick(Bouquets bouquet) {
        favoriteBouquets.add(bouquet); // Добавление выбранного букета в список избранных

        adapter.notifyDataSetChanged(); // Уведомляем адаптер о изменении данных
    }
}