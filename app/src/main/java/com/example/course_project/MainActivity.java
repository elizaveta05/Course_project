package com.example.course_project;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Настройка баннера
        ViewAnimator viewAnimator = findViewById(R.id.ViewAnimator);
        // Устанавливаем анимации перехода
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
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

        // Добавляем обработчики для ручного перелистывания
        viewAnimator.setOnClickListener(v -> {
            viewAnimator.showNext();
            handler.removeCallbacks(runnable); // Удаляем предыдущий автоматический Runnable
            handler.postDelayed(runnable, 3000); // Запускаем новый автоматический Runnable
        });

        handler.postDelayed(runnable, 3000); // Запускаем первое переключение через 3 секунды
        //получение данных с бд
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Popular bouquets").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Bouquets> bouquetsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bouquets bouquet = document.toObject(Bouquets.class);
                        bouquetsList.add(bouquet);
                    }
                    setAdapterData(bouquetsList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e);
                    Toast.makeText(MainActivity.this, "Failed to get data from database", Toast.LENGTH_SHORT).show();
                });
    }

        private void setAdapterData(ArrayList<Bouquets> bouquetsList) {
            LinearLayout llContent = findViewById(R.id.ll_content);
            llContent.removeAllViews(); // Очищаем все предыдущие элементы

            for (Bouquets bouquet : bouquetsList) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.main_cataloge, llContent, false);
                ImageView imageView = itemView.findViewById(R.id.imageView);
                TextView productName = itemView.findViewById(R.id.ProductName);
                TextView productPrice = itemView.findViewById(R.id.ProductPrice);

                Picasso.get().load(bouquet.getImage()).into(imageView);
                productName.setText(bouquet.getName());
                productPrice.setText(String.format("%s ₽", bouquet.getPrice()));

                llContent.addView(itemView);
            }
        }

}