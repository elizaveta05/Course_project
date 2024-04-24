package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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

        // Инициализация FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Привязка к компонентам на макете
        tt_phone = findViewById(R.id.tt_phone);
        tt_code = findViewById(R.id.tt_code);
        btn_regis = findViewById(R.id.btn_regis);
        btn_add = findViewById(R.id.btn_add);

        // Скрытие кнопки регистрации и блокировка поля для кода
        btn_regis.setVisibility(View.GONE);
        tt_code.setEnabled(false);

        // Настройка обработчиков клика для кнопок навигации
        setupNavigationButtons();

        // Настройка авторизации пользователя по номеру телефона
        setupPhoneAuthentication();
    }

    private void setupNavigationButtons() {
        ImageButton btn_back = findViewById(R.id.btn_account);
        btn_back.setOnClickListener(v -> goToActivity(activity_account.class));

        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> goToActivity(MainActivity.class));

        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> goToActivity(Category.class));

        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> navigateToFavorite());

        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> goToActivity(Shop.class));
    }

    // Стандартизированный метод для перехода к указанной активности
    private void goToActivity(Class<?> cls) {
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
    private void setupPhoneAuthentication() {
        // Настройка отправки кода подтверждения на номер телефона
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

        // Настройка ввода кода подтверждения и авторизации
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

    // Сallbacks для PhoneAuthProvider для обработки верификации номера
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // Обработка ошибок верификации номера телефона
            handleVerificationFailure(e);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            handleCodeSent(verificationId);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    // Обработка результата аутентификации
                    handleAuthenticationResult(task);
                });
    }

    private void handleVerificationFailure(FirebaseException e) {
        // Обработка типов ошибок верификации номера телефона
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(Authorization.this, "Некорректный формат номера телефона", Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseTooManyRequestsException) {
            Toast.makeText(Authorization.this, "Превышен лимит запросов на верификацию. Попробуйте позже", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Authorization.this, "Произошла ошибка верификации номера телефона", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCodeSent(String verificationId) {
        this.verificationId = verificationId;
        Toast.makeText(Authorization.this, "Код верификации отправлен", Toast.LENGTH_SHORT).show();

        tt_code.setEnabled(true);
        tt_code.setFocusable(true);
        btn_add.setVisibility(View.GONE);
        btn_regis.setVisibility(View.VISIBLE);
    }

    private void handleAuthenticationResult(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = task.getResult().getUser();
            Toast.makeText(Authorization.this, "Аутентификация пользователя успешна", Toast.LENGTH_SHORT).show();
            goToActivity(MainActivity.class);
        } else {
            Toast.makeText(Authorization.this, "Ошибка аутентификации с помощью SMS", Toast.LENGTH_SHORT).show();
        }
    }
}