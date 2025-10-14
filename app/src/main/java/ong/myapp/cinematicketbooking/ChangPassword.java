package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ChangPassword extends AppCompatActivity {
    private Button btnXacnhan;
    private TextInputEditText Pass_old, Pass_new, Pass_new_confirm;
    private TextInputLayout layoutPass_old, layoutPass_new, layoutPass_new_confirm;
    private FirebaseFirestore db;
    private String email, pass_old, userId;
    private ImageButton exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chang_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        exit = findViewById(R.id.exit); // Đảm bảo ánh xạ trước khi sử dụng
        exit.setOnClickListener(view -> finish());
        db = FirebaseFirestore.getInstance();
        email = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("userId");
        anhxA();
    }

    private void anhxA() {
        exit = findViewById(R.id.exit);
        Pass_new = findViewById(R.id.Pass_new);
        Pass_old = findViewById(R.id.Pass_old);
        layoutPass_new = findViewById(R.id.layoutPass_new);
        layoutPass_old = findViewById(R.id.layoutPass_old);
        layoutPass_new_confirm = findViewById(R.id.layoutPass_new_confirm);
        Pass_new_confirm = findViewById(R.id.Pass_new_confirm); // Sửa id thành id đúng
        btnXacnhan = findViewById(R.id.btnDoiMK);
        btnXacnhan.setOnClickListener(view -> CheckPassword());
    }

    private void CheckPassword() {
        pass_old = Pass_old.getText().toString();
        String pass_new = Pass_new.getText().toString();
        String pass_new_confirm = Pass_new_confirm.getText().toString();

        // Kiểm tra mật khẩu cũ
        if (pass_old.isEmpty()) {
            layoutPass_old.setError("Vui lòng nhập mật khẩu cũ!");
            return;
        } else {
            layoutPass_old.setError(null); // Xóa lỗi nếu có
        }

        // Kiểm tra mật khẩu mới
        if (pass_new.isEmpty()) {
            layoutPass_new.setError("Vui lòng nhập mật khẩu mới!");
            return;
        } else {
            layoutPass_new.setError(null); // Xóa lỗi nếu có
        }

        // Kiểm tra mật khẩu xác nhận
        if (pass_new_confirm.isEmpty()) {
            layoutPass_new_confirm.setError("Vui lòng nhập xác nhận mật khẩu mới!");
            return;
        } else {
            layoutPass_new_confirm.setError(null); // Xóa lỗi nếu có
        }

        // Kiểm tra mật khẩu mới khớp với xác nhận
        if (!pass_new.equals(pass_new_confirm)) {
            layoutPass_new_confirm.setError("Mật khẩu mới không khớp!");
            return;
        } else {
            layoutPass_new_confirm.setError(null); // Xóa lỗi nếu có
        }

        // Tiến hành kiểm tra mật khẩu cũ trong Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("email", email)
                .whereEqualTo("password", pass_old)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            String userId = querySnapshot.getDocuments().get(0).getId();
                            db.collection("User").document(userId)
                                    .update("password", pass_new)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Đã xảy ra lỗi khi cập nhật mật khẩu!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            layoutPass_old.setError("Mật khẩu cũ không chính xác!");
                        }
                    } else {
                        Toast.makeText(this, "Đã xảy ra lỗi. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}