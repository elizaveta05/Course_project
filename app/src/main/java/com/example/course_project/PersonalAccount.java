package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User currentUser; // Объект текущего пользователя


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        // Инициализация Firebase Auth и Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();

            // Получение данных о текущем пользователе из Firestore
            db.collection("Users").document(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        String phone = document.getString("phone");

                        if (name != null && date != null && phone != null) {
                            currentUser = new User(name, date, phone);

                            // Отображение приветствия с именем пользователя
                            TextView welcomeTextView = findViewById(R.id.tv_welcome);
                            welcomeTextView.setText("Добро пожаловать, " + currentUser.getName() + "!");
                        } else {
                            Toast.makeText(PersonalAccount.this, "Some data is null or missing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PersonalAccount.this, "No data found for current user", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PersonalAccount.this, "Error reading user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Настройка слушателей нажатия для кнопок навигации и действий пользователя
        setupButtonListeners();
    }

    // Метод для настройки обработчиков нажатия на кнопки
    private void setupButtonListeners() {
        ImageButton btn_back = findViewById(R.id.btn_account);
        btn_back.setOnClickListener(v -> navigateTo(MainActivity.class));

        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> navigateTo(MainActivity.class));

        ImageButton btn_catalog = findViewById(R.id.btn_cataloge);
        btn_catalog.setOnClickListener(v -> navigateTo(Category.class));

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
        btn_shop.setOnClickListener(v -> navigateTo(Shop.class));

        Button btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(v -> navigateTo(Profile.class));

        Button btn_order = findViewById(R.id.btn_order);
        btn_order.setOnClickListener(v -> navigateTo(KlientOrder.class));

        Button btn_magazine = findViewById(R.id.btn_magazine);
        btn_magazine.setOnClickListener(v -> navigateTo(List_of_stores.class));
    }
    // Метод для навигации на другой экран
    private void navigateTo(Class<?> destinationClass) {
        Intent intent = new Intent(this, destinationClass);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
    }
}