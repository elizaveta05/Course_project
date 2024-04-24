package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class View_categories extends AppCompatActivity implements BouquetsAdapter.FavoritesListener {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Bouquets> bouquetsList = new ArrayList<>();
    private BouquetsAdapter adapter; // Объявление объекта адаптера
    private RecyclerView recyclerView;
    private  FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.ll_content);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Настройка кнопок навигации
        setupClickListeners();

        // Получение данных о выбранной категории и отображение названия
        String categoryName = getIntent().getStringExtra("categoryName");
        TextView tv_name = findViewById(R.id.tv_result);
        tv_name.setText(categoryName);

        // Загрузка букетов по выбранной категории
        loadBouquetsByCategory(categoryName);
    }
    // Настройка обработчиков клика для кнопок
    private void setupClickListeners() {
        // Устанавливаем обработчики кликов для кнопок навигации
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> startNewActivity(Shop.class));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

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


    }

    // Метод для запуска новой активности
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
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

    // Загрузка букетов по выбранной категории из Firestore
    private void loadBouquetsByCategory(String category) {
        db.collection("Categories")
                .whereEqualTo("Name", category)
                .get()
                .addOnCompleteListener(categoryTask -> {
                    if (categoryTask.isSuccessful()) {
                        for (QueryDocumentSnapshot categoryDoc : categoryTask.getResult()) {
                            DocumentReference categoryRef = categoryDoc.getReference();

                            db.collection("Bouquets")
                                    .whereEqualTo("Category", categoryRef)
                                    .get()
                                    .addOnCompleteListener(bouquetsTask -> {
                                        if (bouquetsTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : bouquetsTask.getResult()) {
                                                Bouquets bouquet = document.toObject(Bouquets.class);
                                                bouquet.setId(document.getId());
                                                bouquetsList.add(bouquet);
                                            }
                                            setupRecyclerView();
                                        } else {
                                            Toast.makeText(View_categories.this, "Ошибка загрузки данных о букетах", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(View_categories.this, "Ошибка загрузки категории", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Настройка RecyclerView с использованием адаптера
    private void setupRecyclerView() {
        adapter = new BouquetsAdapter(this, bouquetsList, this);
        recyclerView.setAdapter(adapter);
        // Обновление RecyclerView
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFavoriteClick(Bouquets bouquet) {

    }
}