package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Bouquets_cards extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String bouquetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouquets_cards);
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Favorite.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });
        // Получаем информацию о букете из Intent
        bouquetId = getIntent().getStringExtra("bouquet_id");
        getBouquetById(bouquetId);
    }

    private void getBouquetById(String bouquetId) {
        db.collection("Bouquets")
                .document(bouquetId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Bouquets selectedBouquet = task.getResult().toObject(Bouquets.class);

                            DocumentReference categoryRef = selectedBouquet.getCategory();
                            categoryRef.get()
                                    .addOnSuccessListener(categoryDocumentSnapshot -> {
                                        if (categoryDocumentSnapshot.exists()) {
                                            String categoryName = categoryDocumentSnapshot.getString("Name");

                                            DocumentReference compositionRef = selectedBouquet.getComposition();
                                            compositionRef.get()
                                                    .addOnSuccessListener(compositionDocumentSnapshot -> {
                                                        if (compositionDocumentSnapshot.exists()) {
                                                            String compositionName = compositionDocumentSnapshot.getString("Name");
                                                            showBouquetDetails(selectedBouquet, categoryName, compositionName);
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            Toast.makeText(Bouquets_cards.this, "Bouquet not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Bouquets_cards.this, "Error loading bouquet data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBouquetDetails(Bouquets selectedBouquet, String categoryName, String compositionName) {
        ImageView imageView = findViewById(R.id.imageView6);
        TextView tvName = findViewById(R.id.tv_name_bouquets);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvCost = findViewById(R.id.tv_cost);
        TextView tvComposition = findViewById(R.id.tv_composition);
        TextView tvDescription = findViewById(R.id.tv_description);

        Picasso.get().load(selectedBouquet.getImage()).into(imageView);
        tvName.setText(selectedBouquet.getName());
        tvCategory.setText(String.format("Категория: %s", categoryName)); // Устанавливаем имя категории
        tvCost.setText(String.format("Стоимость: %s₽", selectedBouquet.getCost()));
        tvComposition.setText(String.format("Состав букета: %s", compositionName)); // Устанавливаем имя композиции
        tvDescription.setText(String.format("Описание: %s", selectedBouquet.getDescription()));
    }

}