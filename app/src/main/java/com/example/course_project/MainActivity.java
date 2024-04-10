package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка обработчика клика для кнопки "Аккаунт"
        ImageButton btn_account = findViewById(R.id.btn_back);
        btn_account.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Пользователь найден, переходим на экран PersonalAccount
                Intent intent = new Intent(this, PersonalAccount.class);
                startActivity(intent);
            } else {
                // Пользователь не найден, переходим на экран activity_account
                Intent intent = new Intent(this, activity_account.class);
                startActivity(intent);
            }
        });
        // Настройка обработчиков клика для кнопки "Избранное"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Favorite.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShoppingCart.class);
            startActivity(intent);
        });
        // Настройка обработчиков клика для кнопки "Каталог"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Category.class);
            startActivity(intent);
        });
        // Настройка баннера с анимацией перехода
        ViewAnimator viewAnimator = findViewById(R.id.ViewAnimator);
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {
                viewAnimator.showNext();
                i++;
                if (i >= viewAnimator.getChildCount()) {
                    i = 0;
                }
                handler.postDelayed(this, 3000); // Задержка в миллисекундах (3000 = 3 секунды)
            }
        };

        // Обработчик клика для ручного перелистывания баннера
        viewAnimator.setOnClickListener(v -> {
            viewAnimator.showNext();
            handler.removeCallbacks(runnable); // Удаляем предыдущий автоматический Runnable
            handler.postDelayed(runnable, 3000); // Запускаем новый автоматический Runnable
        });

        handler.postDelayed(runnable, 3000); // Запускаем первое переключение через 3 секунды

        // Получение данных из базы данных Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Инициализация объекта для взаимодействия с Firestore

        // Получение коллекции "Popular bouquets" из Firestore и добавление слушателя успеха и ошибки
        db.collection("Popular bouquets").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Bouquets> bouquetsList = new ArrayList<>(); // Создание списка для хранения данных о букетах

                    // Итерация по документам, полученным из коллекции в успешном запросе
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bouquets bouquet = document.toObject(Bouquets.class); // Преобразование документа в объект класса Bouquets
                        bouquetsList.add(bouquet); // Добавление букета в список
                    }

                    setAdapterData(bouquetsList); // Вызов метода для установки данных в адаптер
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e); // Логирование ошибки получения данных из базы данных
                    Toast.makeText(MainActivity.this, "Failed to get data from database", Toast.LENGTH_SHORT).show(); // Отображение сообщения об ошибке через Toast
                });
    }

    // Метод для установки данных в GridLayout адаптер
    private void setAdapterData(ArrayList<Bouquets> bouquetsList) {
        GridLayout gridLayout = findViewById(R.id.ll_content); // Получение ссылки на GridLayout с id ll_content
        gridLayout.removeAllViews(); // Очищаем все предыдущие элементы из GridLayout

        // Итерация по списку букетов для отображения каждого букета
        for (Bouquets bouquet : bouquetsList) {
            // Создание нового представления элемента каталога из макета main_cataloge
            View itemView = LayoutInflater.from(this).inflate(R.layout.main_cataloge, gridLayout, false);

            // Получение ссылок на компоненты ImageView, TextView для отображения изображения букета, названия и цены
            ImageView imageView = itemView.findViewById(R.id.imageView);
            TextView productName = itemView.findViewById(R.id.ProductName);
            TextView productPrice = itemView.findViewById(R.id.ProductPrice);

            // Загрузка изображения букета с использованием библиотеки Picasso и отображение в ImageView
            Picasso.get().load(bouquet.getImage()).into(imageView);

            // Установка текста названия букета в соответствующий TextView
            productName.setText(bouquet.getName());

            // Установка текста цены букета с добавлением символа валюты ₽ в соответствующий TextView
            productPrice.setText(String.format("%s ₽", bouquet.getPrice()));

            // Установка параметров компонентов GridLayout для представления элемента
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Устанавливаем ширину столбца

            // Установка параметров представления элемента и добавление его в GridLayout
            itemView.setLayoutParams(params);
            gridLayout.addView(itemView);
        }
    }
}