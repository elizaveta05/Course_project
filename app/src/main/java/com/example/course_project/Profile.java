package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Инициализация Firestore и Firebase Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            userRef = db.collection("Users").document(currentUserId);

            // Привязка EditText полей профиля
            editTextName = findViewById(R.id.editTextName);
            editTextDate = findViewById(R.id.editTextDate);
            editTextPhone = findViewById(R.id.editTextPhone);
            // Запрет на редактирование полей
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            editTextPhone.setEnabled(false);

            // Получение данных о текущем пользователе из Firestore
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        String phone = document.getString("phone");

                        // Заполнение EditText данными из Firestore
                        editTextName.setText(name);
                        editTextDate.setText(date);
                        editTextPhone.setText(phone);
                    }
                } else {
                    Toast.makeText(Profile.this, "Ошибка при получении данных: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Настройка кнопок редактирования, сохранения, выхода и удаления аккаунта
            setupButtonListeners();
            // Настройка обработчиков клика для кнопок
            setupClickListeners();
        }
    }

    // Настройка обработчиков клика для кнопок
    private void setupButtonListeners() {
        ImageButton btn_edit = findViewById(R.id.btn_edit);
        ImageButton btn_save = findViewById(R.id.btn_save);
        btn_save.setVisibility(View.GONE);
        btn_edit.setOnClickListener(v -> enableEditProfileFields());

        btn_save.setOnClickListener(v -> saveProfileChanges());

        Button logoutButton = findViewById(R.id.btn_exit);
        logoutButton.setOnClickListener(v -> logoutUser());

        Button deleteAccountButton = findViewById(R.id.btn_delete);
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
    }
    // Настройка обработчиков клика для кнопок
    private void setupClickListeners() {
        // Устанавливаем обработчики кликов для кнопок навигации
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> startNewActivity(MainActivity.class));

        ImageButton btn_favorite = findViewById(R.id.btn_favorite);
        btn_favorite.setOnClickListener(v -> navigateToFavorite());

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> startNewActivity(Shop.class));

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> startNewActivity(PersonalAccount.class));

    }

    // Метод для запуска новой активности
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(0, 0); // Убрать анимацию перехода
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
    // Редактирование полей профиля
    private void enableEditProfileFields() {
        editTextName.setEnabled(true);
        editTextDate.setEnabled(true);
        findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
    }

    // Сохранение изменений профиля
    private void saveProfileChanges() {
        String name = editTextName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        // Проверка и сохранение данных
        if (isValidProfileData(name, date)) {
            userRef.update("name", name, "date", date)
                    .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Profile.this, "Ошибка при обновлении данных: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        editTextName.setEnabled(false);
        editTextDate.setEnabled(false);
        findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_save).setVisibility(View.GONE);
    }

    // Валидация данных профиля
    private boolean isValidProfileData(String name, String date) {
        if (name.isEmpty() || name.length() < 3 || name.length() > 50 || date.isEmpty() || !date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            Toast.makeText(Profile.this, "Ошибка валидации данных", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Выход из аккаунта
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Profile.this, MainActivity.class));
        overridePendingTransition(0, 0);
    }

    // Удаление аккаунта пользователя
    private void deleteAccount() {
        userRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser.delete().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        Toast.makeText(Profile.this, "Профиль удален", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Profile.this, MainActivity.class));
                    }
                });
            }
        });
    }
}