package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {

    private TextView dotsTextView;
    private Handler handler = new Handler();
    private int dotCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dotsTextView = findViewById(R.id.dots);

        // Tạo hiệu ứng cho các chấm '...'
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i <= dotCount; i++) {
                    dots.append(".");
                }
                dotsTextView.setText(dots.toString());

                dotCount++;
                if (dotCount > 3) {
                    dotCount = 0;
                }

                handler.postDelayed(this, 500);
            }
        }, 500);

        // Bắt đầu tải dữ liệu
        loadData();
    }

    // Phương thức để tải dữ liệu
    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Movie")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        moveToMainActivity();

                    } else {

                    }
                });
    }

    // Phương thức chuyển sang MainActivity
    private void moveToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
