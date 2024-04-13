package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        ImageButton btn_account = findViewById(R.id.btn_back);
        btn_account.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Пользователь найден, переходим на экран PersonalAccount
                Intent intent = new Intent(this, PersonalAccount.class);
                startActivity(intent);
            } else {
                // Пользователь не найден, переходим на экран activity_account
                Intent intent = new Intent(this, activity_account.class);
                startActivity(intent);
            }
        });
        ImageButton btnReadyBouquets = findViewById(R.id.btn_ready_bouquets);
        btnReadyBouquets.setOnClickListener(v -> loadBouquetsByCategory("Готовые букеты"));

        ImageButton btnBirthdayBouquets = findViewById(R.id.btn_birthday_bouquets);
        btnBirthdayBouquets.setOnClickListener(v -> loadBouquetsByCategory("Букеты ко дню рождения"));

        ImageButton btnBasketBouquets = findViewById(R.id.btn_bouquets_basket);
        btnBasketBouquets.setOnClickListener(v -> loadBouquetsByCategory("Букеты в корзине"));

        ImageButton btnBoxBouquets = findViewById(R.id.btn_bouquets_box);
        btnBoxBouquets.setOnClickListener(v -> loadBouquetsByCategory("Букеты в коробке"));

        ImageButton btnChildBouquets = findViewById(R.id.btn_bouquets_child);
        btnChildBouquets.setOnClickListener(v -> loadBouquetsByCategory("Детские букеты"));

        ImageButton btnMonoBouquets = findViewById(R.id.btn_monobouquets);
        btnMonoBouquets.setOnClickListener(v -> loadBouquetsByCategory("Монобукеты"));

        ImageButton btnWeddingBouquets = findViewById(R.id.btn_wedding_bouquets);
        btnWeddingBouquets.setOnClickListener(v -> loadBouquetsByCategory("Свадебные букеты"));

        ImageButton btnSale = findViewById(R.id.btn_sale);
        btnSale.setOnClickListener(v -> loadBouquetsByCategory("Акция"));
    }

    private void loadBouquetsByCategory(String category) {
        Intent intent = new Intent(this, View_categories.class);
        intent.putExtra("categoryName", category);
        startActivity(intent);
    }

}




