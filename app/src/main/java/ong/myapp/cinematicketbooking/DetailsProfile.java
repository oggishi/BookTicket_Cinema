package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ong.myapp.cinematicketbooking.model.User;

public class DetailsProfile extends AppCompatActivity {

    private TextView tVName, tVDate, tVPhone, tVEmail, tVgender;
    private Button txtChangePassword;
    private String userId, email; // This should be passed from previous Activity
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_profile); // Replace with your XML file name
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Ánh xạ view từ XML
        tVName = findViewById(R.id.edtName); // Sửa id theo XML nếu cần
        tVDate = findViewById(R.id.edtDate);
        tVPhone = findViewById(R.id.edtPhone);
        tVEmail = findViewById(R.id.edtEmail);
        tVgender = findViewById(R.id.gender);
        txtChangePassword = findViewById(R.id.txtChangePassword);

        // Lấy userId từ Intent
        userId = getIntent().getStringExtra("userId");

        String name = getIntent().getStringExtra("name");
        String dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        String phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        String gender = getIntent().getStringExtra("gender");
        tVName.setText(name);
        tVDate.setText(dateOfBirth);
        tVPhone.setText(phone); // Chuyển int thành String
        tVEmail.setText(email);
        tVgender.setText(gender);
        // Xử lý sự kiện đổi mật khẩu
        txtChangePassword.setOnClickListener(view -> {
            // Tạo Intent để chuyển sang Activity ChangPassword
            Intent intent = new Intent(DetailsProfile.this, ChangPassword.class);
            intent.putExtra("email", email); // Lấy email từ TextView
            intent.putExtra("userId", userId); // Lấy userId từ Intent
            startActivity(intent);
        });
    }

}
