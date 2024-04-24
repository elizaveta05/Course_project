package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShoppingCart2 extends AppCompatActivity {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText etName, etPhone;
    private Spinner spDate, spTime, sp_magazine;
    private RecyclerView recyclerView;
    Adapter_list_order adapter;
    Button btn_checkout;
    Switch switch1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart2);

        switch1 = findViewById(R.id.switch1);
        switch1.setChecked(true); // Устанавливаем переключатель в состояние "true"

        if (!(switch1.isChecked())) {
            Intent intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }


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
                        Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    });
        }
        //Заполняем доступные даты и время доставки, магазины
        spDate = findViewById(R.id.sp_date);
        spTime = findViewById(R.id.sp_time);
        sp_magazine = findViewById(R.id.sp_magazine);

        // Заполнение sp_date ближайшими 10 днями
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Для sp_date
        ArrayAdapter<String> dateAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, dateList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(dateAdapter);

    // Получаем данные из коллекции "Store" и заполняем Spinner
        db.collection("Store")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> storeAddresses = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String address = documentSnapshot.getString("Address");
                        storeAddresses.add(address);
                    }

                    ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeAddresses);
                    storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_magazine.setAdapter(storeAdapter);

                    //  слушатель для выбора магазина и получения времени доставки
                    sp_magazine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedAddress = (String) parent.getItemAtPosition(position);

                            db.collection("TimeOfDeliveryOfTheStore")
                                    .whereEqualTo("Store", selectedAddress)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        List<String> PickupTimes = new ArrayList<>();

                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String pickupTime = documentSnapshot.getString("PickupTime");
                                            PickupTimes.add(pickupTime);
                                        }

                                        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(ShoppingCart2.this, android.R.layout.simple_spinner_item, PickupTimes);
                                        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spTime.setAdapter(timeAdapter);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Обработка ошибки получения данных о времени доставки из коллекции "TimeOfDeliveryOfTheStore"
                                        Toast.makeText(ShoppingCart2.this, "Error fetching delivery time data", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Toast.makeText(ShoppingCart2.this, "Выберите магазин для просмотра времени доставки", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки получения данных из коллекции "Store"
                    Toast.makeText(this, "Error fetching store data", Toast.LENGTH_SHORT).show();
                });

    }

    private abstract class OnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    }
}