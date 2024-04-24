package com.example.course_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class KlientOrder extends AppCompatActivity {
    RecyclerView recyclerView = findViewById(R.id.recyclerView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klient_order);


    }
}
