package com.example.course_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("searchQuery")) {
            String query = intent.getStringExtra("searchQuery");
            filterBouquets(query);
        }
    }

    private void filterBouquets(String query) {
        db.collection("Bouquets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bouquets> filteredBouquets = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bouquets bouquet = document.toObject(Bouquets.class);
                        if (bouquet.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredBouquets.add(bouquet);
                        }
                    }
                    updateSearchResults(filteredBouquets.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Failed to get data from database", e);
                    Toast.makeText(SearchResult.this, "Failed to get data from database", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateSearchResults(int count) {
        TextView tvSearchResults = findViewById(R.id.tv_result);
        String text = "Результаты по поиску: " + count;
        tvSearchResults.setText(text);

        Toast.makeText(SearchResult.this, "Найдено " + count + " букетов", Toast.LENGTH_SHORT).show();

    }
}