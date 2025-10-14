package ong.myapp.cinematicketbooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import ong.myapp.cinematicketbooking.model.Seat;
import ong.myapp.cinematicketbooking.model.Ticket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class ThanhToanActivity extends AppCompatActivity {
    private ImageView posterImageView;
    private TextView movieNameTextView, subTypeTextView, ageLimitTextView, numberTicketTextView,sumTextView,
            theaterTextView, showTimeTextView, dateTextView, timerTextView,seatsValueTextView, priceTextView;
    private Button btnPay;
    private RadioButton zalopayRadioButton,momoRadioButton,shopeepayRadioButton;
    private RadioGroup paymentRadioGroup;
    private String showId, seatValue, payMethod = "", todayDate,userId,date,movieId,showTime; // Biến để lưu phương thức thanh toán
    private int selectedId = 0;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        ImageButton btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());
        ArrayList<String> listSeat = getIntent().getStringArrayListExtra("selectedSeats");

        posterImageView = findViewById(R.id.poster);
        numberTicketTextView = findViewById(R.id.numberTicket);
        movieNameTextView = findViewById(R.id.movieName);
        subTypeTextView = findViewById(R.id.subType);
        ageLimitTextView = findViewById(R.id.age_limit);
        theaterTextView = findViewById(R.id.theaterName);
        showTimeTextView = findViewById(R.id.time);
        dateTextView = findViewById(R.id.date);
        seatsValueTextView = findViewById(R.id.seatsValue);
        timerTextView = findViewById(R.id.timer);
        sumTextView = findViewById(R.id.amount);
        priceTextView = findViewById(R.id.price);
        startCountdownTimer();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        todayDate = dateFormat.format(calendar.getTime());

        String title = getIntent().getStringExtra("title");
        String coverPhoto = getIntent().getStringExtra("coverPhoto");
        String subType = getIntent().getStringExtra("subType");
        String ageLimit = getIntent().getStringExtra("ageLimit");
        String theaterId = getIntent().getStringExtra("theaterId");
        showId = getIntent().getStringExtra("showId");
        movieId = getIntent().getStringExtra("movieId");
        userId = getIntent().getStringExtra("userId");
        showTime = getIntent().getStringExtra("showTime");
        date = getIntent().getStringExtra("date");
        String numberTicket = getIntent().getStringExtra("numberTicket");
        String sum = getIntent().getStringExtra("sum");
        ArrayList<String> selectedList = getIntent().getStringArrayListExtra("selectedSeats");

        Glide.with(ThanhToanActivity.this)
                .load(coverPhoto)
                .into(posterImageView);
        // Set text cho các TextView
        movieNameTextView.setText(title);
        subTypeTextView.setText(subType);
        ageLimitTextView.setText(ageLimit);
        showTimeTextView.setText(showTime);
        dateTextView.setText(date);
        numberTicketTextView.setText(selectedList.size()+"x ghế : ");

        sumTextView.setText(sum);
        priceTextView.setText(sum);
        theaterTextView.setText(theaterId);

        StringBuilder seatsBuilder = new StringBuilder();

        for (int i = 0; i < selectedList.size(); i++) {
            seatsBuilder.append(selectedList.get(i));
            if (i < selectedList.size() - 1) {
                seatsBuilder.append(", ");
            }
        }
        seatValue = seatsBuilder.toString();
        seatsValueTextView.setText(seatsBuilder.toString());

        RadioGroup radioGroup = findViewById(R.id.paymentRadioGroup); // Thay thế bằng ID của RadioGroup từ XML
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                String paymentMethod = "";

                // Lấy tên của phương thức thanh toán và hiển thị Toast
                if (selectedRadioButton != null) {
                    paymentMethod = selectedRadioButton.getText().toString();
                    Toast.makeText(ThanhToanActivity.this, "Selected: " + paymentMethod, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Khởi tạo các view
        zalopayRadioButton = findViewById(R.id.zalopay_radio_button);
        momoRadioButton = findViewById(R.id.momo_radio_button);
        shopeepayRadioButton = findViewById(R.id.shopeepay_radio_button);
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup);


        // Đặt sự kiện cho RadioGroup
        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Kiểm tra nút nào được chọn và gán giá trị của TextView tương ứng vào payMethod
                if (checkedId == R.id.zalopay_radio_button) {
                    payMethod = "ZaloPay";
                } else if (checkedId == R.id.momo_radio_button) {
                    payMethod = "Momo";
                } else if (checkedId == R.id.shopeepay_radio_button) {
                    payMethod = "Shopeepay";
                }
                selectedId = 1;
            }
        });



        btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList != null && !selectedList.isEmpty()) {
                    if (selectedId == 0) {
                        Toast.makeText(getApplicationContext(), "Vui lòng chọn phương thức thanh toán.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        new AlertDialog.Builder(ThanhToanActivity.this)
                                .setTitle("Xác nhận")
                                .setMessage("Bạn có chắc chắn muốn đặt vé không?")
                                .setPositiveButton("Đồng ý", (dialog, which) -> {

                                    ArrayList<String> listSeatId = new ArrayList<>();
                                    String ticketId ="T" + generateRandom10Digits();
                                // Lặp qua danh sách ghế đã chọn và lưu vào Firestore
                                for (String seatValue : selectedList) {
                                    String seatId = seatValue + "_" + showId + "_" + showTime;
                                    Seat seatObject = new Seat(seatId, seatValue, showId, showTime, userId);
                                    seatObject.saveToFirestore(); // Lưu đối tượng Seat vào Firestore
                                    listSeatId.add(seatValue);
                                }
                                Ticket ticket = new Ticket(ticketId, listSeatId, userId, todayDate, showTime, date, movieId);
                                ticket.saveToFirestore();

                                // Hiển thị thông báo thành công
                                Toast.makeText(ThanhToanActivity.this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show();

                                // Chuyển sang màn hình PaymentDone
                                Intent intent = new Intent(ThanhToanActivity.this, PaymentDone.class);

                                intent.putExtra("MOVIE_NAME", title);
                                intent.putExtra("userId", userId);
                                intent.putExtra("SUB_TYPE", subType);
                                intent.putExtra("AGE_LIMIT", ageLimit);
                                intent.putExtra("THEATER", theaterId);
                                intent.putExtra("NUMBER_TICKET", numberTicket);
                                intent.putExtra("SEATS_VALUE", seatValue);
                                intent.putExtra("TIME", showTime);
                                intent.putExtra("DATE", date);
                                intent.putExtra("PAY_METHOD", payMethod);
                                intent.putExtra("AMOUNT_DONE", sum);
                                intent.putExtra("DATE_BOOK", todayDate);
                                startActivity(intent);
                                })
                            .setNegativeButton("Hủy", (dialog, which) -> {
                                // Đóng hộp thoại
                                dialog.dismiss();
                            })
                            .show();
                    }
                }
            }
        });

    }

    private void startCountdownTimer() {
        int startTimeInSeconds = 420; // 7 minutes in seconds
        new CountDownTimer(startTimeInSeconds * 1000, 1000) { // 1000ms = 1 second
            public void onTick(long millisUntilFinished) {
                // Tính toán phút và giây
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;

                // Cập nhật TextView
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(timeFormatted);
            }

            public void onFinish() {
                // Khi thời gian đếm ngược kết thúc
                timerTextView.setText("00:00");
                // Hiển thị thông báo
                Toast.makeText(getApplicationContext(), "Đã hết thời gian giữ vé!", Toast.LENGTH_SHORT).show();
//                // Quay lại trang trước
                finish();
            }
        }.start();
    }

    public static String generateRandom10Digits() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        // Tạo 10 chữ số ngẫu nhiên
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10); // Random từ 0 đến 9
            randomNumber.append(digit);
        }

        return randomNumber.toString();
    }

}
