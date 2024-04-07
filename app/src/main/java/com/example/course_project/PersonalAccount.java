package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String currentUserId = currentUser.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    if (userName != null) {
                        TextView welcomeTextView = findViewById(R.id.tv_welcome);
                        welcomeTextView.setText("Добро пожаловать, " + userName + "!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении данных из базы данных
                Log.e("Authorization", "Ошибка при чтении данных пользователя: " + databaseError.getMessage());
            }
        });

        Button btn_profile=findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(v->{
            Intent intent = new Intent(PersonalAccount.this, Profile.class);
            startActivity(intent);
        });

        Button btn_order=findViewById(R.id.btn_order);
        btn_order.setOnClickListener(v->{
            Intent intent = new Intent(PersonalAccount.this, KlientOrder.class);
            startActivity(intent);
        });

        Button btn_magazine=findViewById(R.id.btn_magazine);
        btn_magazine.setOnClickListener(v->{
            Intent intent = new Intent(PersonalAccount.this, Shop.class);
            startActivity(intent);
        });
    }
}