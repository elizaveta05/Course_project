package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            Intent intent = new Intent(this, PersonalAccount.class);
            startActivity(intent);
        });
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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


        ImageButton btn_save= findViewById(R.id.btn_save);
        btn_save.setVisibility(View.GONE); // Скрываем кнопку "Сохранения изменений"
        ImageButton btn_edit= findViewById(R.id.btn_edit);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

            // Инициализация полей EditText
            editTextName = findViewById(R.id.editTextName);
            editTextDate = findViewById(R.id.editTextDate);
            editTextPhone = findViewById(R.id.editTextPhone);
            // Закрытие EditText
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            editTextPhone.setEnabled(false);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String date = dataSnapshot.child("date").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        // Заполнение полей профиля данными из Firebase
                        editTextName.setText(name);
                        editTextDate.setText(date);
                        editTextPhone.setText(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибок при чтении данных из базы данных
                }
            });
            btn_edit.setOnClickListener(v->{
                // Открытие EditText для редактирования
                editTextName.setEnabled(true);
                editTextDate.setEnabled(true);
                btn_save.setVisibility(View.VISIBLE); // Открываем кнопку "Сохранения изменений"
            });
            btn_save.setOnClickListener(v -> {
                String name = editTextName.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();

                // Проверка данных
                if (name.isEmpty() || name.length() < 3 || name.length() > 50 ||
                        date.isEmpty() || !date.matches("^\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d$")) {
                    // Проверки данных не прошли, не сохраняем и не обновляем номер телефона
                    Toast.makeText(Profile.this, "Ошибка валидации данных", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    // Сохранение данных в Firebase Realtime Database
                    usersRef.child("name").setValue(name);
                    usersRef.child("date").setValue(date);
                    Toast.makeText(Profile.this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                }

                // Закрытие текстовых полей и скрытие кнопки сохранения
                editTextName.setEnabled(false);
                editTextDate.setEnabled(false);
                btn_edit.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);

            });

            Button logoutButton = findViewById(R.id.btn_exit);
            logoutButton.setOnClickListener(v -> {
                // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });

            Button deleteAccountButton = findViewById(R.id.btn_delete);
            deleteAccountButton.setOnClickListener(v -> {
                // Удаление аккаунта из Firebase
                usersRef.removeValue();
                currentUser.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Профиль удален!", Toast.LENGTH_SHORT).show();
                            }
                        });
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }
    }
}