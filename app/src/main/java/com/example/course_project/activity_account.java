package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class activity_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Button btnAuthorization = findViewById(R.id.btn_authorization);
        btnAuthorization.setOnClickListener(v->{
            Intent intent = new Intent(this,Authorization.class);
            startActivity(intent);
        });
        Button btnRegistration = findViewById(R.id.btn_registration);
        btnRegistration.setOnClickListener(v->{
            Intent intent = new Intent(this,Registration.class);
            startActivity(intent);
        });
    }
}