package com.example.course_project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BouquetsAdapter extends RecyclerView.Adapter<BouquetsAdapter.BouquetsViewHolder> {
    private static Context context;
    private ArrayList<Bouquets> bouquetsList;
    private ArrayList<Bouquets> favoriteBouquetsList = new ArrayList<>();
    private ArrayList<Bouquets> buyBouquetsList = new ArrayList<>();

    private FavoritesListener favoritesListener;
    private static FirebaseFirestore db;
    public BouquetsAdapter(Context context, ArrayList<Bouquets> bouquetsList, FavoritesListener favoritesListener) {
        this.context = context;
        this.bouquetsList = bouquetsList;
        this.favoritesListener = favoritesListener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BouquetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_bouquets, parent, false);
        return new BouquetsViewHolder(view);
    }

    public void setBouquetsListFavority(ArrayList<Bouquets> bouquets) {//Назначаем полученный ищбранный список как основной лист
        bouquetsList = bouquets;
    }
    public ArrayList<Bouquets> getFavoriteBouquetsListFavority() {//Получение списка избранных
        fetchFavoriteBouquetsByUser();
        return favoriteBouquetsList;
    }

    @Override
    public void onBindViewHolder(@NonNull BouquetsViewHolder holder, int position) {
        Bouquets bouquet = bouquetsList.get(position);
        holder.bind(bouquet);
        // Создаем универсальный обработчик нажатия
        View.OnClickListener onClickListener = v -> {
            Intent intent = new Intent(context, Bouquets_cards.class);
            intent.putExtra("bouquet_id", bouquet.getId());
            context.startActivity(intent);
        };

        // Применяем обработчик ко всем нужным элементам
        holder.itemView.setOnClickListener(onClickListener);
        holder.imageView.setOnClickListener(onClickListener);
        holder.productName.setOnClickListener(onClickListener);
        holder.productPrice.setOnClickListener(onClickListener);

       /* holder.btnBuy.setOnClickListener(v->{
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                Bouquets selectedBouquet = bouquetsList.get(position);
                if(selectedBouquet!=null) {
                    addToOrderCart(selectedBouquet);
                } else {Toast.makeText(context, "Ошибка!", Toast.LENGTH_SHORT).show();}

            }else {Toast.makeText(context, "Войдите в аккаунт", Toast.LENGTH_SHORT).show();}
        });
*/

        holder.btnFavorite.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (bouquet != null) {
                    if (bouquet.getId() != null) {
                        db.collection("FavoriteList")
                                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .whereEqualTo("bouquetId", bouquet.getId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        removeBouquetFromFavorites(bouquet);
                                        // selectedBouquet.setFavorite(false);
                                        Toast.makeText(context, "Запрос на удаление!", Toast.LENGTH_SHORT).show();
                                    } else if (queryDocumentSnapshots.isEmpty()) {
                                        Toast.makeText(context, "Запрос на добавление!", Toast.LENGTH_SHORT).show();
                                        addBouquetToFavorites(bouquet);
                                        //bouquet.setFavorite(true);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Ошибка!" + e, Toast.LENGTH_SHORT).show();
                                });
                    } else if (bouquet.getId() == null) {
                        Toast.makeText(context, "Ошибка! Не найден ID букета!", Toast.LENGTH_SHORT).show();
                    }
                } else if (bouquet == null) {
                    Toast.makeText(context, "Ошибка! Букет не найден!", Toast.LENGTH_SHORT).show();
                }
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(context, "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            }
        });
    }
        public int getItemCount() {
            return bouquetsList.size();
        }

        public static class BouquetsViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView productName;
            TextView productPrice;
            ImageButton btnFavorite;
            ImageButton btnBuy;

            public BouquetsViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                productName = itemView.findViewById(R.id.ProductName);
                productPrice = itemView.findViewById(R.id.ProductPrice);
                btnFavorite = itemView.findViewById(R.id.btn_favorite);
                btnBuy = itemView.findViewById(R.id.btnbuy);
            }


            public void bind(Bouquets bouquet) {
            Picasso.get().load(bouquet.getImage()).into(imageView);
            productName.setText(bouquet.getName());
            productPrice.setText(String.format("%s ₽", bouquet.getCost()));

            db.collection("FavoriteList")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("bouquetId", bouquet.getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {//Если букет уже добавлен в избранное
                            bouquet.setFavorite(true);
                        } else {//Если отсутсвует в избранном
                            bouquet.setFavorite(false);
                        }
                        if (bouquet.getFavorite()) {
                            btnFavorite.setImageResource(R.mipmap.ikon_favorite_add);
                        } else if (!(bouquet.getFavorite())) {
                            btnFavorite.setImageResource(R.mipmap.ikon_favorite);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Ошибка!" + e, Toast.LENGTH_SHORT).show();
                    });

        }
    }
    public interface FavoritesListener {
        void onFavoriteClick(Bouquets bouquet);
    }

    public void addToOrderCart(Bouquets bouquet) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String bouquetId = bouquet.getId();

        if (bouquetId != null) {
            OrderCart order = new OrderCart(userId, bouquetId);
            db.collection("ListOrder")
                    .add(order)
                    .addOnSuccessListener(docRef -> {
                        notifyDataSetChanged();  // Обновляем адаптер
                        Toast.makeText(context, "Букет добавлен в корзину!", Toast.LENGTH_SHORT).show();
                        Log.d("ORDER", "Bouquet added to order successfully!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ORDER", "Error adding bouquet to order", e);
                        Toast.makeText(context, "Error adding bouquet to order", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.d("Debug", "Bouquet ID is null");
        }
    }

    public void addBouquetToFavorites(Bouquets bouquet) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String bouquetId = bouquet.getId();

        if (bouquetId != null) {
            FavoriteItem favoriteItem = new FavoriteItem(userId, bouquetId);

            db.collection("FavoriteList")
                    .add(favoriteItem)
                    .addOnSuccessListener(docRef -> {
                        favoriteBouquetsList.add(bouquet);
                        fetchFavoriteBouquetsByUser();
                        notifyDataSetChanged();  // Обновляем адаптер
                        Toast.makeText(context, "Букет добавлен в избранное!", Toast.LENGTH_SHORT).show();
                        Log.d("FAVORITES", "Bouquet added to favorites successfully!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FAVORITES", "Error adding bouquet to favorites", e);
                        Toast.makeText(context, "Букет не добавлен в избранное!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.d("Debug", "Bouquet ID is null");
        }
    }

    public void removeBouquetFromFavorites(Bouquets bouquet) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String bouquetId = bouquet.getId();

        db.collection("FavoriteList")
                .whereEqualTo("userId", userId)
                .whereEqualTo("bouquetId", bouquetId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        db.collection("FavoriteList").document(doc.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    favoriteBouquetsList.remove(bouquet);
                                    fetchFavoriteBouquetsByUser();
                                    notifyDataSetChanged();
                                    Log.d("FAVORITES", "Bouquet removed from favorites successfully!");
                                    Toast.makeText(context, "Букет удален из избранного!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FAVORITES", "Error removing bouquet from favorites", e);
                                    Toast.makeText(context, "Букет не удален из избранного!", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("FAVORITES", "Error getting documents", e));
    }


    public void fetchFavoriteBouquetsByUser() {
        favoriteBouquetsList.clear(); // Очищаем список избранных букетов

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("FavoriteList")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String bouquetId = documentSnapshot.getString("bouquetId");
                        if (bouquetId != null) {
                            db.collection("Bouquets")
                                    .document(bouquetId)
                                    .get()
                                    .addOnSuccessListener(bouquetDocument -> {
                                        if (bouquetDocument.exists()) {
                                            Bouquets favoriteBouquet = bouquetDocument.toObject(Bouquets.class);
                                            favoriteBouquet.setId(bouquetDocument.getId());
                                            if (favoriteBouquet != null) {
                                                favoriteBouquetsList.add(favoriteBouquet);
                                                //favoriteBouquet.setFavorite(true);
                                            }
                                            notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FAVORITES", "Error getting favorite bouquets for user", e));
    }
  }