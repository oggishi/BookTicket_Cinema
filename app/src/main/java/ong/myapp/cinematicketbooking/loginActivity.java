package ong.myapp.cinematicketbooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ong.myapp.cinematicketbooking.model.Movie;
import ong.myapp.cinematicketbooking.model.User;

public class loginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPassword;
    private TextInputLayout layoutEmail, layoutPassword;
    private Button btnDangnhap;
    private ImageButton exit;
    private TextView tvQuenmk, register;
    private List<User> userList;
    private String email;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        userList = new ArrayList<>();
        loadUsersFromFirestore();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        anhXa();
        exit.setOnClickListener(view -> finish());
        btnDangnhap.setOnClickListener(view -> validateAndLoginUser());
        tvQuenmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginActivity.this, QuenMatKhauActivity.class);
                startActivity(intent);
            }
        });register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });

    }
    private void anhXa(){
        tvQuenmk=findViewById(R.id.tvQuenmk);
        register=findViewById(R.id.register);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnDangnhap = findViewById(R.id.btnHoantat);
        exit = findViewById(R.id.exit);

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
                            }
                        } else {
                            Log.w("Firestore", "Error getting users.", task.getException());
                        }
                    }
                });
    }
    private void validateAndLoginUser() {

        email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();


        layoutEmail.setError(null);
        layoutPassword.setError(null);


        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Vui lòng nhập email hợp lệ");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            String role = querySnapshot.getDocuments().get(0).getString("role");
                            Intent intent = null;

                            if ("admin".equals(role)) {
                                // Chuyển tới HomeActivity dành cho admin
                                intent = new Intent(loginActivity.this, AdminActivity.class);
                            } else {
                                // Chuyển tới HomeActivity dành cho người dùng thông thường
                                intent = new Intent(loginActivity.this, UserActivity.class);
                                intent.putExtra("userId", getUserIdByEmail(userList,email));
                            }
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Đã xảy ra lỗi. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserIdByEmail(List<User> userList, String email) {
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                return user.getUserId();
            }
        }
        return null;
    }

}
