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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
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

        // Настройка обработчиков клика для кнопок
        setupClickListeners();

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
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Обработка ошибок верификации номера телефона
                Toast.makeText(Registration.this, "Ошибка верификации номера телефона: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Некорректный формат номера телефона
                    Toast.makeText(Registration.this, "Некорректный формат номера телефона", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // Превышение лимита запросов на верификацию
                    Toast.makeText(Registration.this, "Превышен лимит запросов на верификацию. Попробуйте позже", Toast.LENGTH_SHORT).show();
                } else {
                    // Другие типы ошибок
                    Toast.makeText(Registration.this, "Произошла ошибка верификации номера телефона", Toast.LENGTH_SHORT).show();
                }
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
        btn_back.setOnClickListener(v -> startNewActivity(activity_account.class));

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
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("date", date);
                            userMap.put("phone", phone);

                            db.collection("Users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registration.this, "Данные пользователя успешно записаны", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Registration.this, MainActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0); // Убрать анимацию перехода
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Registration.this, "Ошибка записи данных пользователя: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
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