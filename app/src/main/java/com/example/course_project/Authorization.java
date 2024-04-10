package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authorization extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private EditText tt_phone, tt_code;
    private Button btn_regis, btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mAuth = FirebaseAuth.getInstance();
        tt_phone = findViewById(R.id.tt_phone);
        tt_code = findViewById(R.id.tt_code);
        btn_regis = findViewById(R.id.btn_regis);
        btn_add = findViewById(R.id.btn_add);

        btn_regis.setVisibility(View.GONE); // Скрываем кнопку "Зарегистрироваться"
        tt_code.setEnabled(false); // Блокируем поле для ввода кода

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
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

        // Настройка авторизации пользователя по номеру телефона
        btn_add.setOnClickListener(v -> {
            String phoneNumber = tt_phone.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(Authorization.this, "Пожалуйста, введите номер телефона", Toast.LENGTH_SHORT).show();
            } else {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60L,
                        TimeUnit.SECONDS,
                        Authorization.this,
                        mCallbacks
                );
            }
        });
        btn_regis.setOnClickListener(v -> {
            String verificationCode = tt_code.getText().toString().trim();
            if (!verificationCode.isEmpty()) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(Authorization.this, "Пожалуйста, введите код верификации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Authorization.this, "Ошибка верификации номера телефона", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Authorization.this.verificationId = verificationId;
            mResendToken = forceResendingToken;
            Toast.makeText(Authorization.this, "Код верификации отправлен", Toast.LENGTH_SHORT).show();

            tt_code.setEnabled(true);
            btn_add.setVisibility(View.GONE);
            btn_regis.setVisibility(View.VISIBLE);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(Authorization.this, "Аутентификация пользователя успешна", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Authorization.this, MainActivity.class));
                    } else {
                        Toast.makeText(Authorization.this, "Ошибка аутентификации с помощью SMS", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}