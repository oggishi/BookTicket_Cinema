package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetailsTicket extends AppCompatActivity {
    private TextView titleTextView;
    private TextView timeTextView;
    private TextView idTextView;
    private TextView dateTextView;
    private TextView dateBookTextView;
    private TextView quantityTextView;
    private TextView amountTextView;
    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_ticket);

        // Initialize Views
        titleTextView = findViewById(R.id.title);
        timeTextView = findViewById(R.id.time);
        dateTextView = findViewById(R.id.date);
        dateBookTextView = findViewById(R.id.dateBook);
        quantityTextView = findViewById(R.id.quantity);
        amountTextView = findViewById(R.id.amount);
        posterImageView = findViewById(R.id.poster);
        idTextView = findViewById(R.id.id);

        Intent intent = getIntent();
        String showTime = intent.getStringExtra("showTime");
        String moviePoster = intent.getStringExtra("moviePoster");
        String movieTitle = intent.getStringExtra("movieTitle");
        String dateBook = intent.getStringExtra("dateBook");
        String date = intent.getStringExtra("date");
        String amount = intent.getStringExtra("amount");
        String quantity = intent.getStringExtra("quantity");
        String ticketId = intent.getStringExtra("ticketId");

        titleTextView.setText(movieTitle);
        timeTextView.setText(showTime);
        dateTextView.setText(date);
        dateBookTextView.setText(dateBook);
        quantityTextView.setText(quantity);
        amountTextView.setText(amount);
        idTextView.setText(ticketId);

        Glide.with(DetailsTicket.this)
                .load(moviePoster)
                .error(R.drawable.joker) // Hình ảnh lỗi tạm thời
                .into(posterImageView);


    }


}
