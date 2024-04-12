package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private User currentUser; // Объект текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        // Получаем экземпляр FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();

            usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String date = dataSnapshot.child("date").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        if (name != null && date != null && phone != null) {
                            currentUser = new User(name, date, phone);

                            TextView welcomeTextView = findViewById(R.id.tv_welcome);
                            welcomeTextView.setText("Добро пожаловать, " + currentUser.getName() + "!");
                        } else {
                            Toast.makeText(PersonalAccount.this,"Some data is null or missing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PersonalAccount.this,"No data found for current user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PersonalAccount.this,"Error reading user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });

        }

        // Установка обработчиков нажатия для кнопок
        Button btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalAccount.this, Profile.class);
            startActivity(intent);
        });

        Button btn_order = findViewById(R.id.btn_order);
        btn_order.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalAccount.this, KlientOrder.class);
            startActivity(intent);
        });

        Button btn_magazine = findViewById(R.id.btn_magazine);
        btn_magazine.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalAccount.this, List_of_stores.class);
            startActivity(intent);
        });
        ImageButton btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            Intent intent = new Intent(this, MainActivity.class);
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

    }
}