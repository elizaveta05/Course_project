package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText tt_name, tt_date, tt_phone;
    private CheckBox cb_assent;
    private String name, date, phone, verificationId = "";
    private boolean hasAssent;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        tt_name = findViewById(R.id.tt_name);
        tt_date = findViewById(R.id.tt_date);
        tt_phone = findViewById(R.id.tt_phone);
        cb_assent = findViewById(R.id.cb_assent);

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
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnRegister = findViewById(R.id.btn_regis);
        EditText editTextCode = findViewById(R.id.tt_code);

        btnRegister.setVisibility(View.GONE); // Скрываем кнопку "Зарегистрироваться"
        editTextCode.setEnabled(false);

        btnAdd.setOnClickListener(v -> {
            name = tt_name.getText().toString().trim();
            date = tt_date.getText().toString().trim();
            phone = tt_phone.getText().toString().trim();
            hasAssent = cb_assent.isChecked();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                Toast.makeText(Registration.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else if (!hasAssent) {
                Toast.makeText(Registration.this, "Подтвердите согласие на обработку данных", Toast.LENGTH_SHORT).show();
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

                editTextCode.setEnabled(true);
                editTextCode.requestFocus();
                btnAdd.setVisibility(View.GONE);;
                btnRegister.setVisibility(View.VISIBLE);;

                btnRegister.setOnClickListener(v -> {
                    String inputCode = editTextCode.getText().toString().trim();

                    if (TextUtils.isEmpty(inputCode)) {
                        Toast.makeText(Registration.this, "Введите полученный код", Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, inputCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                });
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            User newUser = new User(name, date, phone);
                            databaseRef.setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Registration.this, "Данные пользователя успешно записаны", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Registration.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Registration.this, "Ошибка записи данных пользователя: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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