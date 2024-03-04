package com.example.course_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ViewAnimator;
import android.os.Bundle;


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
    }
}