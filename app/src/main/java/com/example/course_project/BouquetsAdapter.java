package com.example.course_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BouquetsAdapter extends RecyclerView.Adapter<BouquetsAdapter.BouquetsViewHolder> {

    private Context context; // Контекст приложения
    private ArrayList<Bouquets> bouquetsList; // Список всех букетов
    private ArrayList<Bouquets> favoriteBouquetsList = new ArrayList<>(); // Список избранных букетов
    private FavoritesListener favoritesListener; // Обработчик нажатия на избранное

    public BouquetsAdapter(Context context, ArrayList<Bouquets> bouquetsList, FavoritesListener favoritesListener) {
        this.context = context;
        this.bouquetsList = bouquetsList;
        this.favoritesListener = favoritesListener;
    }

    @NonNull
    @Override
    public BouquetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_cataloge, parent, false); // Создаем новое представление View из макета
        return new BouquetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BouquetsViewHolder holder, int position) {
        Bouquets bouquet = bouquetsList.get(position); // Получение букета по позиции
        holder.bind(bouquet); // Установка данных букета во ViewHolder

        holder.btnFavorite.setOnClickListener(v -> { // Обработчик нажатия на кнопку "Избранное"
            Bouquets selectedBouquet = bouquetsList.get(holder.getAdapterPosition());
            if (!favoriteBouquetsList.contains(selectedBouquet)) {
                favoriteBouquetsList.add(selectedBouquet); // Если букет не в списке избранных, добавляем его
            } else {
                favoriteBouquetsList.remove(selectedBouquet); // Если букет уже в списке избранных, удаляем его
            }
            notifyItemChanged(holder.getAdapterPosition()); // Обновляем только один элемент
        });
    }

    @Override
    public int getItemCount() {
        return bouquetsList.size(); // Возвращает количество элементов в списке
    }

    public class BouquetsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productName;
        TextView productPrice;
        ImageButton btnFavorite;

        public BouquetsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.ProductName);
            productPrice = itemView.findViewById(R.id.ProductPrice);
            btnFavorite = itemView.findViewById(R.id.btn_favority);
        }

        public void bind(Bouquets bouquet) {
            Picasso.get().load(bouquet.getImage()).into(imageView); // Загружаем изображение букета
            productName.setText(bouquet.getName()); // Устанавливаем название букета
            productPrice.setText(String.valueOf(bouquet.getCost())); // Устанавливаем цену букета

            // Устанавливаем изображение кнопки "Избранное" в зависимости от наличия в списке избранных
            if (favoriteBouquetsList.contains(bouquet)) {
                btnFavorite.setImageResource(R.mipmap.ikon_favorite_add); // Если букет уже в списке избранных
            } else {
                btnFavorite.setImageResource(R.mipmap.ikon_favorite); // Если букет не в списке избранных
            }
        }
    }

    public interface FavoritesListener {
        void onFavoriteClick(Bouquets bouquet); // Интерфейс для обработки нажатия на "Избранное"
    }
}