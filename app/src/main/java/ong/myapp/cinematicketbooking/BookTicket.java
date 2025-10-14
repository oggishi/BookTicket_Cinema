package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ong.myapp.cinematicketbooking.adapter.SeatAdapter;
import ong.myapp.cinematicketbooking.model.User;

public class BookTicket extends AppCompatActivity {

    private RecyclerView recyclerViewSeats;
    private List<String> seatList;
    private List<User> userList;
    private ImageButton btnBack;
    private Button btnContinue;
    private TextView numberTicketTextView, sumTexView, tvTime, movieNamTextView, subTypeTextView, durationTextView, ageLimtTextView, selectedSeatTextView;
    private SeatAdapter seatAdapter;
    private String showTime, showId,userId,movieId;
    private String dateOfBirth = "";
    private int userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ticket);

        // Khởi tạo các thành phần giao diện
        initViews();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        showTime = intent.getStringExtra("time");
        String title = intent.getStringExtra("title");
        String duration = intent.getStringExtra("duration");
        String ageLimit = intent.getStringExtra("ageLimit");
        showId = intent.getStringExtra("showId");
        userId = intent.getStringExtra("userId");
        movieId = intent.getStringExtra("movieId");
        String subType = intent.getStringExtra("subType");
        String theaterId = intent.getStringExtra("theaterId");
        String date = intent.getStringExtra("date");
        String coverPhoto = intent.getStringExtra("coverPhoto");

        // Thiết lập thông tin phim
        setMovieInfo(title, subType, duration, ageLimit);

        // Khởi tạo danh sách ghế
        initSeatList();
        loadSeatsFromFirestore(showId);

        // Xử lý sự kiện nút Tiếp tục
        userList = new ArrayList<>();
        loadUsersFromFirestore();
        assert ageLimit != null;
        int ageLimitation = parseAgeLimit(ageLimit);
        btnContinue.setOnClickListener(v -> {
            ArrayList<String> selectedSeats = seatAdapter.getSelectedSeats();
            if (!selectedSeats.isEmpty()) {
                if(userAge >= ageLimitation){
                    Intent paymentIntent = new Intent(BookTicket.this, ThanhToanActivity.class);
                    paymentIntent.putExtra("showId", showId);
                    paymentIntent.putExtra("userId", userId);
                    paymentIntent.putExtra("movieId", movieId);
                    paymentIntent.putExtra("showTime", showTime);
                    paymentIntent.putExtra("title", title);
                    paymentIntent.putExtra("subType", subType);
                    paymentIntent.putExtra("ageLimit", ageLimit);
                    paymentIntent.putExtra("theaterId", theaterId);
                    paymentIntent.putExtra("date", date);
                    paymentIntent.putExtra("coverPhoto", coverPhoto);
                    paymentIntent.putExtra("numberTicket", numberTicketTextView.getText().toString());
                    paymentIntent.putExtra("sum", sumTexView.getText().toString());
                    paymentIntent.putStringArrayListExtra("selectedSeats", selectedSeats);
                    startActivity(paymentIntent);
                }else{
                    Toast.makeText(BookTicket.this, "Phim này không phù hợp với dộ tuổi của bạn.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BookTicket.this, "Vui lòng chọn ít nhất một ghế.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(view -> finish());StringBuilder seatsBuilder = new StringBuilder();

    }

    private int parseAgeLimit(String ageLimit) {
        String limitation = ageLimit.replaceAll("[^\\d]", "");
        return Integer.parseInt(limitation); // Chuyển thành số nguyên
    }


    private int calculateAge(String dateOfBirth) {
        String[] component = dateOfBirth.split("/");
        int day = Integer.parseInt(component[0]);
        int month = Integer.parseInt(component[1]) - 1; // Tháng trong Calendar bắt đầu từ 0
        int year = Integer.parseInt(component[2]);

        Calendar today = Calendar.getInstance();

        // Tạo đối tượng Calendar cho ngày sinh
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month, day);

        // Tính tuổi
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // Điều chỉnh nếu chưa đến ngày sinh trong năm hiện tại
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }



    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);
        numberTicketTextView = findViewById(R.id.numberTicket);
        sumTexView = findViewById(R.id.sum);
        tvTime = findViewById(R.id.time);
        movieNamTextView = findViewById(R.id.movieName);
        subTypeTextView = findViewById(R.id.subType);
        durationTextView = findViewById(R.id.duration);
        ageLimtTextView = findViewById(R.id.age_limit);
        recyclerViewSeats = findViewById(R.id.recyclerViewSeats);
        selectedSeatTextView = findViewById(R.id.selectedSeat);
    }

    private void setMovieInfo(String title, String subType, String duration, String ageLimit) {
        movieNamTextView.setText(title);
        tvTime.setText(showTime);
        subTypeTextView.setText(subType);
        durationTextView.setText(duration);
        ageLimtTextView.setText(ageLimit);
    }

    private void initSeatList() {
        seatList = new ArrayList<>();
        for (char row = 'A'; row <= 'G'; row++) {
            for (int col = 1; col <= 8; col++) {
                seatList.add(row + String.valueOf(col));
            }
        }
    }

    private void loadSeatsFromFirestore(String showId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Seat").whereEqualTo("showId", showId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String,String> seatTimeMap = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String value = document.getString("value");
                            String seatStartTime = document.getString("startTime");

                            // Chỉ thêm ghế vào seatTimeMap nếu startTime khớp với selectedStartTime
                            if (seatStartTime != null && seatStartTime.equals(showTime)) {
                                seatTimeMap.put(value, seatStartTime);
                            }
                        }
                        seatAdapter = new SeatAdapter(BookTicket.this, seatList, seatTimeMap, showTime, numberTicketTextView, sumTexView, selectedSeatTextView);
                        recyclerViewSeats.setLayoutManager(new GridLayoutManager(BookTicket.this, 8));
                        recyclerViewSeats.setAdapter(seatAdapter);
                    } else {
                        Log.w("Firestore", "Error getting seat status.", task.getException());
                    }
                });
    }

    private void loadUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear(); // Xóa danh sách cũ để tránh trùng lặp
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }for (User user : userList) {
                                if (user.getUserId().equals(userId)) {
                                    dateOfBirth = user.getDateOfBirth();
                                    userAge = calculateAge(dateOfBirth);
                                    break; // Dừng vòng lặp khi tìm thấy user
                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting users.", task.getException());
                        }
                    }
                });
    }


}
