package ong.myapp.cinematicketbooking;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentDone extends AppCompatActivity {

    private TextView movieName, dateBook, subType, ageLimit, theaterTextView, numberTicket, seatsValue, time, date, payMethod, amountDone;
    private Button btnFinish;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_done);


        movieName = findViewById(R.id.movieName);
        subType = findViewById(R.id.subType);
        ageLimit = findViewById(R.id.age_limit);
        theaterTextView = findViewById(R.id.theater);
        numberTicket = findViewById(R.id.numberTicket);
        seatsValue = findViewById(R.id.seatsValue);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        payMethod = findViewById(R.id.payMethod);
        amountDone = findViewById(R.id.amount_done);
        dateBook = findViewById(R.id.dateBook);
        btnFinish = findViewById(R.id.btnFinish);

        Intent intent = getIntent();
        String movieTitle = intent.getStringExtra("MOVIE_NAME");
        userId = intent.getStringExtra("userId");
        String subTypeValue = intent.getStringExtra("SUB_TYPE");
        String ageLimitValue = intent.getStringExtra("AGE_LIMIT");
        String theaterValue = intent.getStringExtra("THEATER");
        String numberTicketValue = intent.getStringExtra("NUMBER_TICKET");
        String seatsValueText = intent.getStringExtra("SEATS_VALUE");
        String timeValue = intent.getStringExtra("TIME");
        String dateValue = intent.getStringExtra("DATE");
        String paymentMethod = intent.getStringExtra("PAY_METHOD");
        String totalAmount = intent.getStringExtra("AMOUNT_DONE");
        String dateBookValue = intent.getStringExtra("DATE_BOOK");

        movieName.setText(movieTitle);
        dateBook.setText(dateBookValue);
        subType.setText(subTypeValue);
        ageLimit.setText(ageLimitValue);
        theaterTextView.setText(theaterValue);
        numberTicket.setText(numberTicketValue+ ": ");
        seatsValue.setText(seatsValueText);
        time.setText(timeValue);
        date.setText(" - " + dateValue);
        payMethod.setText(paymentMethod);
        amountDone.setText(totalAmount);

        // Sự kiện click cho nút Hoàn tất
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về màn hình chính hoặc thực hiện hành động khác
                Intent intent = new Intent(PaymentDone.this, UserActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }
}


