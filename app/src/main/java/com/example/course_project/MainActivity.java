package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BouquetsAdapter.FavoritesListener {
    // Инициализация переменных
    private RecyclerView recyclerView;
    private BouquetsAdapter adapter;
    private ArrayList<Bouquets> bouquetsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private  FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase Authentication и Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.ll_content);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Настройка обработчика клика для кнопки "Аккаунт"
        ImageButton btn_account = findViewById(R.id.btn_back);
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

        // Настройка обработчика клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Shop.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        // Настройка обработчика клика для кнопки "Каталог"
        ImageButton btn_catalog = findViewById(R.id.btn_cataloge);
        btn_catalog.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Category.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        // Настройка баннера с анимацией
        ViewAnimator viewAnimator = findViewById(R.id.ViewAnimator);
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                viewAnimator.showNext();
                i++;
                if (i >= viewAnimator.getChildCount()) {
                    i = 0;
                }
                handler.postDelayed(this, 3000); // Задержка в миллисекундах (3000 = 3 секунды)
            }
        };

        // Обработчик клика для ручного перелистывания баннера
        viewAnimator.setOnClickListener(v -> {
            viewAnimator.showNext();
            handler.removeCallbacks(runnable); // Удаляем предыдущий автоматический Runnable
            handler.postDelayed(runnable, 3000); // Запускаем новый автоматический Runnable
        });

        handler.postDelayed(runnable, 3000); // Запускаем первое переключение через 3 секунды
        db.collection("Bouquets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = 0;
                    bouquetsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        count++;
                        if (count % 3 == 0 && bouquetsList.size() < 15) {
                            Bouquets bouquet = document.toObject(Bouquets.class); // Преобразование документа в объект класса Bouquets
                            bouquet.setId(document.getId());
                            bouquetsList.add(bouquet); // Добавление букета в список
                        }
                    }
                    setupRecyclerView(bouquetsList); // Вызов настроек RecyclerView только после получения всех данных
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e);
                });
        // Настройка кликов и обработчиков для различных категорий
        LinearLayout ll_ready_bouquets = findViewById(R.id.ll_ready_bouquets);
        ll_ready_bouquets.setOnClickListener(v -> loadBouquetsByCategory("Готовые букеты"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
        ImageButton btn_rose = findViewById(R.id.btn_rose);
        btn_rose.setOnClickListener(v -> loadBouquetsByCategory("Готовые букеты"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

        LinearLayout ll_basket = findViewById(R.id.ll_basket);
        ll_basket.setOnClickListener(v -> loadBouquetsByCategory("Букеты в корзине"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
        ImageButton btn_basket = findViewById(R.id.btn_basket);
        btn_basket.setOnClickListener(v -> loadBouquetsByCategory("Букеты в корзине"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

        LinearLayout ll_box = findViewById(R.id.ll_box);
        ll_box.setOnClickListener(v -> loadBouquetsByCategory("Букеты в коробке"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
        ImageButton btn_box = findViewById(R.id.btn_box);
        btn_box.setOnClickListener(v -> loadBouquetsByCategory("Букеты в коробке"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

        LinearLayout ll_child = findViewById(R.id.ll_child);
        ll_child.setOnClickListener(v -> loadBouquetsByCategory("Детские букеты"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
        ImageButton btn_child = findViewById(R.id.btn_child);
        btn_child.setOnClickListener(v -> loadBouquetsByCategory("Детские букеты"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода

        LinearLayout ll_sale = findViewById(R.id.ll_sale);
        ll_sale.setOnClickListener(v -> loadBouquetsByCategory("Акция"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
        ImageButton btn_sale = findViewById(R.id.btn_sale);
        btn_sale.setOnClickListener(v -> loadBouquetsByCategory("Акция"));
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }

    // Настройка RecyclerView
    private void setupRecyclerView(ArrayList<Bouquets> bouquetsList) {
        if(bouquetsList !=null){
            adapter = new BouquetsAdapter(this, bouquetsList, this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, "Проблемы с подключением к бд! Не удается загрузить данные!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBouquetsByCategory(String category) {
        Intent intent = new Intent(this, View_categories.class);
        intent.putExtra("categoryName", category);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }
    @Override
    public void onFavoriteClick(Bouquets bouquet) {

    }
}