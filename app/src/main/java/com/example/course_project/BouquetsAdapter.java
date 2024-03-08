package com.example.course_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class BouquetsAdapter extends RecyclerView.Adapter<BouquetsAdapter.BouquetsViewHolder> {

    // Контекст приложения
    private final Context context;

    // Список букетов
    private final ArrayList<Bouquets> bouquetsList;

    // Конструктор адаптера
    public BouquetsAdapter(Context context, ArrayList<Bouquets> bouquetsList) {
        this.context = context;
        this.bouquetsList = bouquetsList;
    }

    @NonNull
    @Override
    // Создание нового ViewHolder при необходимости
    public BouquetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание нового представления View из макета main_cataloge
        View view = LayoutInflater.from(context).inflate(R.layout.main_cataloge, parent, false);
        return new BouquetsViewHolder(view);
    }

    @Override
    // Привязка данных к ViewHolder при прокрутке списка
    public void onBindViewHolder(@NonNull BouquetsViewHolder holder, int position) {
        // Получение букета по позиции
        Bouquets bouquet = bouquetsList.get(position);
        // Вызов метода bind для заполнения ViewHolder данными
        holder.bind(bouquet);
    }

    @Override
    // Возвращает количество элементов в списке
    public int getItemCount() {
        return bouquetsList.size();
    }

    public static class BouquetsViewHolder extends RecyclerView.ViewHolder {
        // Виджеты, отображающие данные букета
        ImageView imageView;
        TextView productName;
        TextView productPrice;

        // Конструктор ViewHolder
        public BouquetsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализация виджетов из макета
            imageView = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.ProductName);
            productPrice = itemView.findViewById(R.id.ProductPrice);
        }

        // Метод для заполнения ViewHolder данными букета
        public void bind(Bouquets bouquet) {
            // Загрузка изображения букета
            Picasso.get().load(bouquet.getImage()).into(imageView);
            // Установка названия букета
            productName.setText(bouquet.getName());
            // Установка цены букета
            productPrice.setText(String.valueOf(bouquet.getPrice()));
        }
    }
}