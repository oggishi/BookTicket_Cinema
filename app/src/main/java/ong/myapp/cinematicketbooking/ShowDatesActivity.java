package ong.myapp.cinematicketbooking;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.adapter.ShowDatesAdapter;

public class ShowDatesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewShowDates;
    private ShowDatesAdapter showDatesAdapter;
    private FirebaseFirestore db;
    private List<String> showDates;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dates);

        // Initialize RecyclerView
        recyclerViewShowDates = findViewById(R.id.recyclerViewShowDates);
        recyclerViewShowDates.setLayoutManager(new LinearLayoutManager(this));

        // Initialize button "Quay lại"
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> onBackPressed());

        // Initialize Firestore and list
        db = FirebaseFirestore.getInstance();
        showDates = new ArrayList<>();
        String movieId = getIntent().getStringExtra("movieId");
        String theaterId = getIntent().getStringExtra("theaterId");

        showDatesAdapter = new ShowDatesAdapter(showDates, movieId, theaterId);
        recyclerViewShowDates.setAdapter(showDatesAdapter);

        // Load show dates from Firestore
        loadShowDatesFromFirestore(movieId, theaterId);
    }

    private void loadShowDatesFromFirestore(String movieId, String theaterId) {
        db.collection("Show")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("theaterId", theaterId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Set<String> dateSet = new HashSet<>();

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String date = document.getString("date");
                                if (date != null) {
                                    dateSet.add(date); // Loại bỏ ngày trùng lặp
                                }
                            }

                            showDates.clear();
                            showDates.addAll(dateSet); // Thêm tất cả các ngày vào danh sách
                            showDatesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle the error
                    }
                });
    }
}


