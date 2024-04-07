package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText tt_name;
    private EditText tt_date;
    private EditText tt_phone;
    private EditText tt_code;
    private CheckBox cb_assent;
    private String name;
    private String date;
    private String phone;
    private String code;
    private boolean hasAssent;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        tt_name = findViewById(R.id.tt_name);
        tt_date = findViewById(R.id.tt_date);
        tt_phone = findViewById(R.id.tt_phone);
        tt_code = findViewById(R.id.tt_code);
        cb_assent = findViewById(R.id.cb_assent);

        // Настройка обработчиков клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Избранное"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Favorite.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Категории"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Registration.this, "Ошибка верификации номера телефона", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verification, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationId = verification;
                Toast.makeText(Registration.this, "Код верификации отправлен", Toast.LENGTH_SHORT).show();

                Button btnRegister = findViewById(R.id.btn_regis);
                Button btnAdd = findViewById(R.id.btn_add);
                EditText editTextCode = findViewById(R.id.tt_code);

                editTextCode.setEnabled(true);
                editTextCode.requestFocus();
                btnAdd.setVisibility(View.GONE);

                btnRegister.setOnClickListener(v -> {
                    String inputCode = editTextCode.getText().toString().trim();

                    if (inputCode.isEmpty()) {
                        Toast.makeText(Registration.this, "Введите полученный код", Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, inputCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                });
            }
        };

        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            name = tt_name.getText().toString().trim();
            date = tt_date.getText().toString().trim();
            phone = tt_phone.getText().toString().trim();
            code = tt_code.getText().toString().trim();
            hasAssent = cb_assent.isChecked();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(Registration.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else if (!hasAssent) {
                Toast.makeText(Registration.this, "Подтвердите согласие на обработку персональных данных", Toast.LENGTH_SHORT).show();
            } else {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,
                        60L,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks
                );
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

                            User newUser = new User(name, date, phone);
                            databaseRef.child(userId).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Registration.this, "Данные пользователя успешно сохранены", Toast.LENGTH_SHORT).show();

                                        // Чтение данных пользователя из базы данных и вывод их в Logcat
                                        DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    String userName = dataSnapshot.child("name").getValue(String.class);
                                                    String userDate = dataSnapshot.child("date").getValue(String.class);
                                                    String userPhone = dataSnapshot.child("phone").getValue(String.class);

                                                    Log.d("UserData", "User Name: " + userName);
                                                    Log.d("UserData", "User Date: " + userDate);
                                                    Log.d("UserData", "User Phone: " + userPhone);
                                                    // Переход на активность MainActivity
                                                    Intent intent = new Intent(Registration.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Log.d("UserData", "Данные пользователя не найдены в базе данных");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("UserData", "Ошибка при чтении данных пользователя: " + databaseError.getMessage());
                                            }

                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Registration.this, "Ошибка при сохранении данных пользователя: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(Registration.this, "Не удалось получить информацию о текущем пользователе", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Registration.this, "Ошибка аутентификации с помощью SMS", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}