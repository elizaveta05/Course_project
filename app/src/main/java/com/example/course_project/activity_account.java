package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class activity_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Button btnAuthorization = findViewById(R.id.btn_authorization);
        btnAuthorization.setOnClickListener(v->{
            Intent intent = new Intent(this,Authorization.class);
            startActivity(intent);
        });
        Button btnRegistration = findViewById(R.id.btn_registration);
        btnRegistration.setOnClickListener(v->{
            Intent intent = new Intent(this,Registration.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
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
        // Настройка обработчиков клика для кнопки "Категории"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
        });
    }
}