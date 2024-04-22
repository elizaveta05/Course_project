package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Shop extends AppCompatActivity{
    // Инициализация переменных
    private RecyclerView recyclerView;
    private AdapterOrder adapter;
    private ArrayList<Bouquets> bouquetsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_shop);


          // Инициализация Firebase Authentication и Firestore
          mAuth = FirebaseAuth.getInstance();
          db = FirebaseFirestore.getInstance();


          setupRecyclerView();
          fetchBouquetsList();

          // Настройка обработчика клика для кнопки "Каталог"
          ImageButton btn_catalog = findViewById(R.id.btn_cataloge);
          btn_catalog.setOnClickListener(v -> {
              Intent intent = new Intent(Shop.this, Category.class);
              startActivity(intent);
              overridePendingTransition(0, 0); // Убрать анимацию перехода
          });
          // Настройка обработчика клика для кнопки "Аккаунт"
          ImageButton btn_account = findViewById(R.id.btn_back);
          btn_account.setOnClickListener(v -> {
              // Переход на экран PersonalAccount, если пользователь авторизован
              // или на экран activity_account, если пользователь не авторизован
              if (currentUser != null) {
                  Intent intent = new Intent(this, PersonalAccount.class);
                  startActivity(intent);
                  overridePendingTransition(0, 0); // Убрать анимацию перехода

              } else {
                  Intent intent = new Intent(this, activity_account.class);
                  startActivity(intent);
                  overridePendingTransition(0, 0); // Убрать анимацию перехода

              }
          });

          // Настройка обработчика клика для кнопки "Избранное"
          ImageButton btn_favorites = findViewById(R.id.btn_favorites);
          btn_favorites.setOnClickListener(v -> {
              // Переход на экран Favorite, если пользователь авторизован
              // или на экран activity_account с сообщением об ошибке через Toast, если пользователь не авторизован
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

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.ll_content);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AdapterOrder(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void fetchBouquetsList() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("ListOrder")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        HashMap<String, Integer> bouquetQuantities = new HashMap<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String bouquetId = document.getString("bouquetId");
                            int count = bouquetQuantities.getOrDefault(bouquetId, 0);
                            bouquetQuantities.put(bouquetId, count + 1);
                        }

                        ArrayList<Bouquets> orderedBouquets = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String bouquetId = document.getString("bouquetId");
                            db.collection("Bouquets")
                                    .document(bouquetId)
                                    .get()
                                    .addOnSuccessListener(bouquetDocument -> {
                                        Bouquets bouquet = bouquetDocument.toObject(Bouquets.class);
                                        if (bouquet != null) {
                                            orderedBouquets.add(bouquet);
                                            if (orderedBouquets.size() == queryDocumentSnapshots.size()) {
                                                adapter = new AdapterOrder(Shop.this, orderedBouquets);
                                                adapter.setBouquetQuantities(bouquetQuantities);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error fetching bouquet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    }

