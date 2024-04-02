package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

    // Объявление переменных
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

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Инициализация элементов интерфейса
        mAuth = FirebaseAuth.getInstance();
        tt_name = findViewById(R.id.tt_name);
        tt_date = findViewById(R.id.tt_date);
        tt_phone = findViewById(R.id.tt_phone);
        tt_code = findViewById(R.id.tt_code);
        cb_assent = findViewById(R.id.cb_assent);

        tt_code.setEnabled(false);

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            Intent intent = new Intent(Registration.this, activity_account.class);
            startActivity(intent);
        });

        Button btn_main=findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v->{
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
        });

        Button btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v->{
            Intent intent = new Intent(Registration.this, Category.class);
            startActivity(intent);
        });


        // Обработчик кнопки "Добавить"
        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v -> {
            name = tt_name.getText().toString().trim();
            date = tt_date.getText().toString().trim();
            phone = tt_phone.getText().toString().trim();
            code = tt_code.getText().toString().trim();
            hasAssent = cb_assent.isChecked();

            // Проверка и выполнение действий
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(Registration.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else if (!hasAssent) {
                Toast.makeText(Registration.this, "Подтвердите согласие на обработку персональных данных", Toast.LENGTH_SHORT).show();
            } else {
                // Проверка номера телефона и отправка SMS
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone, // Номер телефона, который необходимо верифицировать
                        60L, // Время ожидания для верификации в секундах
                        TimeUnit.SECONDS, // Единица времени для времени ожидания
                        this, // Activity, в которой будет отображено диалоговое окно верификации SMS
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                // Метод вызывается при успешной автоматической верификации номера телефона
                                Toast.makeText(Registration.this, "Телефон успешно прошел верификацию", Toast.LENGTH_SHORT).show();
                                signInWithPhoneAuthCredential(credential); // Вызов метода для входа с использованием учетных данных телефона
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                // Метод вызывается, если происходит ошибка верификации номера телефона
                                Toast.makeText(Registration.this, "Ошибка верификации номера телефона", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                // Метод вызывается при успешной отправке кода подтверждения на номер телефона
                                tt_code.setEnabled(true); // Активация поля ввода кода подтверждения

                                if (!code.isEmpty()) {
                                    // Проверка наличия введенного кода подтверждения
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                                    signInWithPhoneAuthCredential(credential); // Вызов метода для входа с использованием учетных данных телефона
                                }
                            }
                        });
            }
        });
    }

    // Метод для аутентификации по телефону
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Попытка входа пользователя с использованием учетных данных телефона
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Успешно прошла аутентификация
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Если пользователь существует
                            String userId = user.getUid();
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

                            // Создание нового пользователя и сохранение в базе данных
                            User newUser = new User(name, date, phone);
                            databaseRef.child(userId).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        // Успешно сохранено
                                        Toast.makeText(Registration.this, "Данные пользователя успешно сохранены", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Ошибка при сохранении данных пользователя
                                        Toast.makeText(Registration.this, "Ошибка при сохранении данных пользователя: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Не удалось получить информацию о текущем пользователе
                            Toast.makeText(Registration.this, "Не удалось получить информацию о текущем пользователе", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Ошибка аутентификации с помощью SMS
                        Toast.makeText(Registration.this, "Ошибка аутентификации с помощью SMS", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}