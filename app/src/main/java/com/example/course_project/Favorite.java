package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity implements BouquetsAdapter.FavoritesListener {
    private BouquetsAdapter adapter; // Объявление объекта адаптера
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.ll_content);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Вызов метода для установки данных в адаптер
        setupRecyclerView();

        // Настройка обработчиков клика для кнопок
        setupClickListeners();
    }

    // Настройка обработчиков клика для кнопок
    private void setupClickListeners() {
        // Устанавливаем обработчики кликов для кнопок навигации
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> startNewActivity(Shop.class));

        ImageButton btn_account = findViewById(R.id.btn_back);
        btn_account.setOnClickListener(v -> navigateToAccount());

    }

    // Метод для запуска новой активности
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }

    // Навигация на экран учетной записи в зависимости от пользователя
    private void navigateToAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(this, PersonalAccount.class);
        } else {
            intent = new Intent(this, activity_account.class);
        }
        startActivity(intent);
    }

    // Настройка RecyclerView
    private void setupRecyclerView() {
        adapter = new BouquetsAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        // Устанавливаем список избранных букетов в адаптер и обновляем RecyclerView
        ArrayList<Bouquets> favoriteBouquetsList = adapter.getFavoriteBouquetsListFavority();
        adapter.setBouquetsListFavority(favoriteBouquetsList);
        adapter.notifyDataSetChanged();
    }

    // Обработчик клика по букету в избранных
    @Override
    public void onFavoriteClick(Bouquets bouquet) {

    }
}