package ong.myapp.cinematicketbooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;

import ong.myapp.cinematicketbooking.adapter.StartTimeAdapter;
import ong.myapp.cinematicketbooking.adapter.TheaterAdapter;
import ong.myapp.cinematicketbooking.model.Show;

public class TheaterListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTheaters;
    private TheaterAdapter theaterAdapter;
    private FirebaseFirestore db;
    private List<String> theaterList;
    private ImageButton btnQuaylai;
    private Button buttonAddShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_list);

        btnQuaylai = findViewById(R.id.btnQuaylai);
        buttonAddShow = findViewById(R.id.buttonAddShow);

        // Handle "Quay lại" button click
        btnQuaylai.setOnClickListener(view -> onBackPressed());

        // Handle "Thêm suất chiếu" button click
        buttonAddShow.setOnClickListener(view -> showAddShowDialog());

        // Initialize RecyclerView
        recyclerViewTheaters = findViewById(R.id.recyclerViewTheaters);
        recyclerViewTheaters.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore and list
        db = FirebaseFirestore.getInstance();
        theaterList = new ArrayList<>();

        // Get movieId from Intent
        String movieId = getIntent().getStringExtra("movieId");

        // Load theaters from Firestore
        loadTheatersFromFirestore(movieId);
    }

    private void loadTheatersFromFirestore(String movieId) {
        db.collection("Show")
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Set<String> theaterSet = new TreeSet<>(); // TreeSet để tự động loại bỏ trùng lặp và sắp xếp theo thứ tự

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String theaterId = document.getString("theaterId");
                                if (theaterId != null) {
                                    theaterSet.add(theaterId); // Thêm theaterId vào TreeSet
                                }
                            }

                            theaterList.clear();
                            theaterList.addAll(theaterSet); // Thêm tất cả từ TreeSet vào theaterList đã sắp xếp và loại bỏ trùng lặp

                            // Initialize the adapter with movieId after loading the data
                            theaterAdapter = new TheaterAdapter(theaterList, movieId);
                            recyclerViewTheaters.setAdapter(theaterAdapter);

                            theaterAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle the error
                        Toast.makeText(TheaterListActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddShowDialog() {
        String movieId = getIntent().getStringExtra("movieId");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm suất chiếu mới");

        // Inflate layout dialog_edit_show
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_show, null);
        builder.setView(dialogView);

        EditText dateStart = dialogView.findViewById(R.id.dateStart);
        RecyclerView recyclerViewStartTime = dialogView.findViewById(R.id.recyclerViewStartTime);
        Spinner spinnerGenreSub = dialogView.findViewById(R.id.spinnerGenreSub);
        Spinner spinnerGenreRap = dialogView.findViewById(R.id.spinnerGenreRap);
        ImageButton btnAddTime = dialogView.findViewById(R.id.btnAddTime);

        // Sử dụng DatePickerDialog khi người dùng nhấn vào EditText ngày
        dateStart.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        dateStart.setText(formattedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Thiết lập Adapter cho RecyclerView để hiển thị các thời gian
        List<String> startTimes = new ArrayList<>();
        StartTimeAdapter startTimeAdapter = new StartTimeAdapter(startTimes);
        recyclerViewStartTime.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStartTime.setAdapter(startTimeAdapter);

        // Thêm thời gian mới
        btnAddTime.setOnClickListener(v -> {
            startTimes.add("");
            startTimeAdapter.notifyItemInserted(startTimes.size() - 1);
        });

        // Thiết lập dữ liệu cho Spinner
        List<String> subTypeOptions = new ArrayList<>();
        subTypeOptions.add("2D PHỤ ĐỀ");
        subTypeOptions.add("2D LỒNG TIẾNG");
        subTypeOptions.add("3D PHỤ ĐỀ");
        subTypeOptions.add("3D LỒNG TIẾNG");
        ArrayAdapter<String> subTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subTypeOptions);
        subTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenreSub.setAdapter(subTypeAdapter);

        // Thiết lập danh sách từ "Rạp 1" đến "Rạp 7" cho spinnerGenreRap
        List<String> theaterOptions = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            theaterOptions.add("Rạp " + i);
        }
        ArrayAdapter<String> theaterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, theaterOptions);
        theaterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenreRap.setAdapter(theaterAdapter);

        builder.setPositiveButton("Lưu", (dialogInterface, which) -> {
            String newDate = dateStart.getText().toString();
            String newSubType = spinnerGenreSub.getSelectedItem().toString();
            String newTheaterId = spinnerGenreRap.getSelectedItem().toString();

            // Kiểm tra xem ngày đã chọn có phải là ngày trong tương lai không
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date selectedDate = sdf.parse(newDate);
                if (selectedDate != null && selectedDate.before(new Date())) {
                    Toast.makeText(TheaterListActivity.this, "Phải chọn ngày tương lai!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(TheaterListActivity.this, "Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy các thời gian từ adapter
            List<String> updatedTimes = new ArrayList<>(startTimes);

            // Sắp xếp lại danh sách trước khi lưu vào Firestore
            Collections.sort(updatedTimes, (time1, time2) -> {
                try {
                    Date date1 = sdf.parse(time1);
                    Date date2 = sdf.parse(time2);
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });

            // Phát sinh showId mới
            SimpleDateFormat idDateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
            String currentDateStr = idDateFormat.format(new Date());

            db.collection("Show")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            int maxSequence = 0;

                            // Tìm số thứ tự lớn nhất hiện có trong các showId
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String existingShowId = document.getString("showId");
                                if (existingShowId != null && existingShowId.startsWith("show_" + currentDateStr)) {
                                    String sequenceStr = existingShowId.substring(12);
                                    try {
                                        int sequence = Integer.parseInt(sequenceStr);
                                        if (sequence > maxSequence) {
                                            maxSequence = sequence;
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // Tăng số thứ tự lên 1 để tạo showId mới
                            String newShowId = String.format("show_%s%04d", currentDateStr, maxSequence + 1);

                            // Thêm dữ liệu mới vào Firestore với showId làm document ID
                            Show newShow = new Show(newShowId,newDate, updatedTimes , movieId,  newTheaterId,newSubType);
                            db.collection("Show")
                                    .document(newShowId) // Sử dụng showId làm document ID
                                    .set(newShow)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(TheaterListActivity.this, "Thêm suất chiếu thành công!", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(TheaterListActivity.this, "Lỗi khi thêm suất chiếu!", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    });
        });

        builder.setNegativeButton("Hủy", (dialogInterface, which) -> {
            // Hủy bỏ dialog, không cần làm gì thêm
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}



