package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(Category.this, MainActivity.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Избранное"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Favorite.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
        });

    }
}