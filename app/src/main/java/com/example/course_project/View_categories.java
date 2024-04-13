package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class View_categories extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Bouquets> bouquetsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);


        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(this, Category.class));
        });
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
        });

        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Favorite.class);
            startActivity(intent);
        });

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
        });

        // Получение данных о категории
        String categoryName = getIntent().getStringExtra("categoryName");
        // Отобразить название категории
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(categoryName);

        // Загрузка букетов по выбранной категории
        loadBouquetsByCategory(categoryName);


    }

    private void loadBouquetsByCategory(String category) {
        db.collection("ListBouquets")
                .whereEqualTo("Category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bouquetsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Bouquets bouquet = document.toObject(Bouquets.class);
                            bouquetsList.add(bouquet);
                        }
                        setAdapterData(bouquetsList);
                    } else {
                        Toast.makeText(View_categories.this, "Error loading bouquets data", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Метод для установки данных в GridLayout адаптер
    private void setAdapterData(ArrayList<Bouquets> bouquetsList) {
        GridLayout gridLayout = findViewById(R.id.ll_content); // Получение ссылки на GridLayout с id ll_content
        gridLayout.removeAllViews(); // Очищаем все предыдущие элементы из GridLayout

        // Итерация по списку букетов для отображения каждого букета
        if (bouquetsList != null && !bouquetsList.isEmpty()) {
            for (Bouquets bouquet : bouquetsList) {
                // Создание нового представления элемента каталога из макета main_cataloge
                View itemView = LayoutInflater.from(this).inflate(R.layout.main_cataloge, gridLayout, false);

                // Получение ссылок на компоненты ImageView, TextView для отображения изображения букета, названия и цены
                ImageView imageView = itemView.findViewById(R.id.imageView);
                TextView productName = itemView.findViewById(R.id.ProductName);
                TextView productPrice = itemView.findViewById(R.id.ProductPrice);

                // Загрузка изображения букета с использованием библиотеки Picasso и отображение в ImageView
                Picasso.get().load(bouquet.getImage()).into(imageView);

                // Установка текста названия букета в соответствующий TextView
                productName.setText(bouquet.getName());

                // Установка текста цены букета с добавлением символа валюты ₽ в соответствующий TextView
                productPrice.setText(String.format("%s ₽", bouquet.getCost()));

                // Установка параметров компонентов GridLayout для представления элемента
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Устанавливаем ширину столбца

                // Установка параметров представления элемента и добавление его в GridLayout
                itemView.setLayoutParams(params);
                gridLayout.addView(itemView);
            }
        } else {
            Toast.makeText(View_categories.this, "Список букетов пуст или не удалось загрузить данные", Toast.LENGTH_SHORT).show();
        }
    }
}


