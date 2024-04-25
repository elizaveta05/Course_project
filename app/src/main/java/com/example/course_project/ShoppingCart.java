package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingCart extends AppCompatActivity implements Adapter_list_order.OrderListener {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText etName, etPhone, et_address, et_door, et_entrance, et_floor, commentarie;
    private TextView textViewTotalCost;
    private Spinner spDate, spTime;
    private RecyclerView recyclerView;
    Adapter_list_order adapter;
    Button btn_checkout;
    Switch switch1;
    ArrayList<Bouquets> bouquetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        et_address=findViewById(R.id.et_address);
        et_door=findViewById(R.id.et_door);
        et_entrance=findViewById(R.id.et_entrance);
        et_floor=findViewById(R.id.et_floor);
        textViewTotalCost=findViewById(R.id.textViewTotalCost);
        commentarie = findViewById(R.id.commit);

        // Настройка обработчика клика для кнопки "Аккаунт"
        ImageButton btn_account = findViewById(R.id.btn_account);
        btn_account.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        });

        // Настройка обработчика клика для кнопки "Избранное"
        ImageButton btn_favorites = findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(this, Favorite.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Убрать анимацию перехода

            } else {
                Intent intent = new Intent(this, activity_account.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Убрать анимацию перехода
                Toast.makeText(this, "Войдите в аккаунт для добавления в избранное", Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка обработчика клика для кнопки "Главный экран"
        ImageButton btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        // Настройка обработчика клика для кнопки "Каталог"
        ImageButton btn_cataloge = findViewById(R.id.btn_cataloge);
        btn_cataloge.setOnClickListener(v -> {
            Intent intent = new Intent(this, Category.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });
        // Настройка обработчика клика для кнопки "Корзина"
        ImageButton btn_shop = findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Убрать анимацию перехода
        });

        switch1 = findViewById(R.id.switch1);
        switch1.setChecked(false); // Устанавливаем переключатель в состояние "true"
        switch1.setOnClickListener(v->{
            if(switch1.isChecked()){
                Intent intent = new Intent(this, ShoppingCart2.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //Заполняем данными пользователя поля
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);

        if(currentUser != null){
            db.collection("Users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String phone = documentSnapshot.getString("phone");

                            // Устанавливаем полученные данные в EditText
                            etName.setText(name);
                            etPhone.setText(phone);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Обработка ошибки получения данных
                        Toast.makeText(ShoppingCart.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    });
        }
        //Заполняем доступные даты и время доставки
        spDate = findViewById(R.id.sp_date);
        spTime = findViewById(R.id.sp_time);

        // Заполнение sp_date ближайшими 10 днями, начиная со следующего дня
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Увеличиваем текущую дату на один день

        for (int i = 0; i < 10; i++) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        // Для sp_date
        ArrayAdapter<String> dateAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, dateList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(dateAdapter);

        // Получение данных из коллекции "DeliveryTime" и добавление в sp_time
        db.collection("DeliveryTime")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> deliveryTimes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String time = document.getString("Time");
                        if (time != null) {
                            deliveryTimes.add(time);
                        }
                    }
                    // Для sp_time
                    ArrayAdapter<String> timeAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, deliveryTimes);
                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTime.setAdapter(timeAdapter);
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки получения данных
                    Toast.makeText(ShoppingCart.this, "Error fetching delivery times", Toast.LENGTH_SHORT).show();
                });

        setupRecyclerView();

        // Получение общей стоимости и установка в TextView
        adapter.getCost(totalCost -> textViewTotalCost.setText(String.format("Итоговая стоимость: %d ₽", totalCost)));

        btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(v -> {
            registation_order();
        });
    }

    private void registation_order() {
        // Проверка наличия текущего пользователя
        if (currentUser != null) {
            String userID = currentUser != null ? currentUser.getUid() : "";
            String com = commentarie != null ? commentarie.getText().toString().trim() : "";
            String totalCost = textViewTotalCost != null ? textViewTotalCost.getText().toString().trim() : "";
            int costValue = 0;
            if (!totalCost.isEmpty()) {
                // Извлечение числового значения из строки стоимости (удаление текста и символов)
                String costString = totalCost.replaceAll("\\D", "");
                try {
                    costValue = Integer.parseInt(costString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // Проверка на нулевые значения перед добавлением в HashMap
            if (!userID.isEmpty() && !com.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                String currentDate = sdf.format(new Date());

                // Создаем данные для нового заказа в коллекции "OrderRegistration"
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("userID", userID);
                orderData.put("date", currentDate);
                orderData.put("typeID", "TVjvNXpFN235qMMPCLe8");
                orderData.put("commit", com);
                orderData.put("totalcost", costValue);

            // Добавляем новый документ в коллекцию "OrderRegistration"
            db.collection("OrderRegistration")
                    .add(orderData)
                    .addOnSuccessListener(orderDocumentRef -> {
                        // Оповещение об успешно оформленном заказе
                        //Toast.makeText(ShoppingCart.this, "Заказ успешно оформлен в OrderRegistration", Toast.LENGTH_SHORT).show();

                        // Получаем ID только что созданного документа OrderRegistration
                        String orderID = orderDocumentRef.getId();

                        // Получаем данные из элементов ввода
                        String addr = et_address.getText().toString().trim();//Наименование улицы
                        String numberhome = et_door.getText().toString().trim();//Номер двери
                        String entrance = et_entrance.getText().toString().trim();//номер подъезда
                        String floor = et_floor.getText().toString().trim();//Номер этажа

                        String address = "Улица: " + addr + ", номер подъезда: " + numberhome + ",этаж: "+entrance+",номер квартиры: "+floor;

                        String timeID = "";
                        if (spTime.getSelectedItem() != null) {
                            timeID = spTime.getSelectedItem().toString(); // Получение выбранного timeID из Spinner
                        }
                        String selectedDate = spDate.getSelectedItem().toString();

                        // Создаем данные для новой записи в коллекции "Delivery"
                        Map<String, Object> deliveryData = new HashMap<>();
                        deliveryData.put("orderID", orderID);
                        deliveryData.put("address", address);
                        deliveryData.put("timeID", timeID);
                        deliveryData.put("date", selectedDate);

                        // Добавляем новую запись в коллекцию "Delivery"
                        db.collection("Delivery")
                                .add(deliveryData)
                                .addOnSuccessListener(deliveryDocRef -> {
                                    // Оповещение об успешно созданной записи в коллекции Delivery
                                    //Toast.makeText(ShoppingCart.this, "Запись в коллекции Delivery успешно создана", Toast.LENGTH_SHORT).show();

                                    // Получаем букеты из корзины и добавляем их в коллекцию "OrderItemsBouquet"
                                    if(bouquetsList!=null){
                                        for (Bouquets bouquet : bouquetsList) {
                                            String bouquetID = bouquet.getId();

                                            // Создаем данные для новой записи в коллекции "OrderItemsBouquet"
                                            Map<String, Object> orderItemData = new HashMap<>();
                                            orderItemData.put("orderID", orderID);
                                            orderItemData.put("bouquetId", bouquetID);

                                            // Добавляем новый документ в коллекцию "OrderItemsBouquet"
                                            db.collection("OrderItemsBouquet")
                                                    .add(orderItemData)
                                                    .addOnSuccessListener(orderItemDocRef -> {
                                                        //Toast.makeText(ShoppingCart.this, "Запись в коллекции OrderItemsBouquet успешно создана", Toast.LENGTH_SHORT).show();
                                                        Log.d("OrderItemsBouquet", "Added order item with ID: " + orderItemDocRef.getId());

                                                        db.collection("ListOrder")
                                                                .whereEqualTo("userId", currentUser)
                                                                .get()
                                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                                        db.collection("ListOrder").document(doc.getId()).delete()
                                                                                .addOnSuccessListener(aVoid -> {
                                                                                    adapter.notifyDataSetChanged();
                                                                                    Log.d("FAVORITES", "Bouquet removed from favorites successfully!");
                                                                                    Toast.makeText(this, "Коризан очищена!", Toast.LENGTH_SHORT).show();
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Log.e("FAVORITES", "Error removing bouquet from favorites", e);
                                                                                    Toast.makeText(this, "Корзина не очищена!", Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(e -> Log.e("FAVORITES", "Error getting documents", e));
                                                        Toast.makeText(ShoppingCart.this, "Заказ успешно создан!", Toast.LENGTH_SHORT).show();
                                                        Intent intent= new Intent(this, MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(0, 0); // Убрать анимацию перехода

                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Ошибка при добавлении записи в коллекции OrderItemsBouquet
                                                        Log.e("OrderItemsBouquet", "Error adding order item: " + e.getMessage());
                                                    });
                                    }}else{Toast.makeText(ShoppingCart.this, "Ошибка! Не получен список букетов!", Toast.LENGTH_SHORT).show();}

                                })
                                .addOnFailureListener(e -> {
                                    // Ошибка при создании записи в коллекции Delivery
                                    Toast.makeText(ShoppingCart.this, "Ошибка при создании записи в коллекции Delivery", Toast.LENGTH_SHORT).show();
                                    Log.e("Delivery", "Error adding delivery document: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Ошибка при добавлении заказа
                        Toast.makeText(ShoppingCart.this, "Ошибка при оформлении заказа", Toast.LENGTH_SHORT).show();
                        Log.e("OrderRegistration", "Error adding order document: " + e.getMessage());
                    });
            } else {
                // Показать сообщение об ошибке недостающих данных
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Пользователь не авторизован
            Toast.makeText(ShoppingCart.this, "Для оформления заказа необходимо войти в аккаунт", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new Adapter_list_order(this, new ArrayList<>(),this);
        recyclerView.setAdapter(adapter);
        // Устанавливаем список избранных букетов в адаптер и обновляем RecyclerView
        bouquetsList = adapter.getfetchBouquetQuantitiesByUser();
        adapter.setfetchBouquetQuantitiesByUser(bouquetsList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Bouquets bouquet) {

    }
    @Override
    public void onCostReceived(int totalCost) {
        runOnUiThread(() -> textViewTotalCost.setText(String.format("Итоговая стоимость: %d ₽", totalCost)));
    }
}


