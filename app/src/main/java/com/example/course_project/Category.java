package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Category extends AppCompatActivity {
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Настройка обработчиков клика для кнопок
        setupClickListeners();
        // Устанавливаем обработчики для кнопок категорий букетов
        setUpCategoryButtons();
        // Настройка SearchView
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    Intent intent = new Intent(Category.this, SearchResult.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // Настройка обработчиков клика для кнопок
    private void setupClickListeners() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Устанавливаем обработчики кликов для кнопок навигации
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));

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

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> startNewActivity(Shop.class));

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

    }

    // Метод для запуска новой активности
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(Category.this, cls);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }

    // Настройка обработчиков для кнопок категорий букетов
    private void setUpCategoryButtons() {
        loadBouquetsByCategory("Готовые букеты", R.id.btn_ready_bouquets);
        loadBouquetsByCategory("Букеты ко дню рождения", R.id.btn_birthday_bouquets);
        loadBouquetsByCategory("Букеты в корзине", R.id.btn_bouquets_basket);
        loadBouquetsByCategory("Букеты в коробке", R.id.btn_bouquets_box);
        loadBouquetsByCategory("Детские букеты", R.id.btn_bouquets_child);
        loadBouquetsByCategory("Монобукеты", R.id.btn_monobouquets);
        loadBouquetsByCategory("Свадебные букеты", R.id.btn_wedding_bouquets);
        loadBouquetsByCategory("Акции", R.id.btn_sale);
    }

    // Загрузка букетов по выбранной категории и переход на экран категорий
    private void loadBouquetsByCategory(String category, int buttonId) {
        ImageButton button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, View_categories.class);
                intent.putExtra("categoryName", category);
                startActivity(intent);
                overridePendingTransition(0, 0); // Убрать анимацию перехода
            });
        } else {
            Log.e("Error", "Button not found with ID: " + buttonId);
        }
    }
}