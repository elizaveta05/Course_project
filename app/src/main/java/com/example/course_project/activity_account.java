package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Настройка обработчиков клика для различных кнопок
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Настройка обработчиков клика для кнопки "Авторизация"
        Button btnAuthorization = findViewById(R.id.btn_authorization);
        btnAuthorization.setOnClickListener(v -> startNewActivity(Authorization.class));

        // Настройка обработчиков клика для кнопки "Регистрация"
        Button btnRegistration = findViewById(R.id.btn_registration);
        btnRegistration.setOnClickListener(v -> startNewActivity(Registration.class));

        // Настройка обработчиков клика для кнопки "Магазин"
        Button btn_magazine = findViewById(R.id.btn_magazine);
        btn_magazine.setOnClickListener(v -> startNewActivity(List_of_stores.class));

        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));

        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> startNewActivity(MainActivity.class));

        // Настройка обработчиков клика для кнопки "Избранное"
        ImageButton btn_favorite = findViewById(R.id.btn_favorite);
        btn_favorite.setOnClickListener(v -> navigateToFavorite());

        // Настройка обработчиков клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> startNewActivity(ShoppingCart.class));

        // Настройка обработчиков клика для кнопки "Категории"
        ImageButton btn_favorites = findViewById(R.id.btn_cataloge);
        btn_favorites.setOnClickListener(v -> startNewActivity(Category.class));
    }
    private void navigateToFavorite() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(this, Favorite.class);
        } else {
            intent = new Intent(this, activity_account.class);
        }
        startActivity(intent);
    }
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }
}