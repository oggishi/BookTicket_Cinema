package ong.myapp.cinematicketbooking;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import ong.myapp.cinematicketbooking.model.User;

public class registerActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputEditText editHovaTen, editEmail,editOTP, editPhone, editNgaySinh, editMatkhau, editXacnhanmatkhau;
    private TextInputLayout layoutHovaTen,layoutOTP, layoutEmail, layoutPhone, layoutMatkhau, layoutXacnhanmatkhau, layoutNgaySinh;
    private RadioGroup radioGroupGender;
    private TextView layoutGenderError;
    private Button btnHoantat, btnOTP;
    private ImageButton exit;
    private String senderPassword;
    private FirebaseRemoteConfig remoteConfig;
    String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        anhXa();

        exit.setOnClickListener(view -> finish());
        btnHoantat.setOnClickListener(view -> validateAndSaveUser());
        btnOTP.setOnClickListener(view -> sendOTP());
        editNgaySinh.setFocusable(false);
        editNgaySinh.setFocusableInTouchMode(false);
        editNgaySinh.setOnClickListener(view -> showDatePickerDialog());
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        fetchRemoteConfig();
        editOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateOTPWithEmail();

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    private void checkEmailExists(final String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User") // Giả sử bạn lưu thông tin người dùng trong collection "users"
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            layoutEmail.setError("Email đã được đăng ký, vui lòng sử dụng email khác");
                            editEmail.requestFocus();

                        } else {
                            sendOTP(email);
                        }
                    }
                });
    }
    private void sendOTP(String email) {
        otp = generateOTP();
        new SendOTPEmailTask(this, senderPassword).execute(email, otp);

    }

    private void validateOTPWithEmail() {
        String enteredOTP = editOTP.getText().toString().trim();
        if (enteredOTP.equals(otp)) {

            layoutOTP.setError(null);
            ColorStateList errorColor = ColorStateList.valueOf(getResources().getColor(R.color.green));
            layoutOTP.setErrorTextColor(errorColor);
            layoutOTP.setError("Xác thực OTP thành công");
            editOTP.setEnabled(false);
        } else {
            layoutOTP.setError("Mã OTP không đúng");
        }
    }


    private void fetchRemoteConfig() {
        remoteConfig.fetch(0)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        remoteConfig.activate().addOnCompleteListener(activateTask -> {
                            if (activateTask.isSuccessful()) {
                                senderPassword = remoteConfig.getString("senderPassword");
                            }

                        });
                    } else {
                        if (task.getException() != null) {
                            Log.e(TAG, "Fetch không thành công: " + task.getException().getMessage());
                        } else {
                            Log.e(TAG, "Fetch không thành công: Lỗi không xác định");
                        }
                    }
                });

    }
    private String generateOTP() {

        int otp = (int) (Math.random() * 1000000);
        return String.format("%06d", otp);
    }

    private void sendOTP() {
        layoutEmail.setError("");
        String email = editEmail.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }
        checkEmailExists(email);

    }


    private void anhXa() {
        layoutHovaTen = findViewById(R.id.layoutHovaTen);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutMatkhau = findViewById(R.id.layoutMatkhau);
        layoutNgaySinh = findViewById(R.id.layoutNgaySinh);
        layoutXacnhanmatkhau = findViewById(R.id.layoutXacnhanmatkhau);
        layoutOTP=findViewById(R.id.layoutOTP);

        editOTP=findViewById(R.id.editOTP);
        editHovaTen = findViewById(R.id.editHovaTen);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editNgaySinh = findViewById(R.id.editNgaySinh);
        editMatkhau = findViewById(R.id.editMatkhau);
        editXacnhanmatkhau = findViewById(R.id.editXacnhanmatkhau);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        layoutGenderError = findViewById(R.id.layoutGenderError);

        btnOTP=findViewById(R.id.btnOTP);
        btnHoantat = findViewById(R.id.btnHoantat);
        exit = findViewById(R.id.exit);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editNgaySinh.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void validateAndSaveUser() {
        String name = editHovaTen.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String dateOfBirth = editNgaySinh.getText().toString().trim();
        String password = editMatkhau.getText().toString().trim();
        String confirmPassword = editXacnhanmatkhau.getText().toString().trim();

        layoutHovaTen.setError(null);
        layoutEmail.setError(null);
        layoutPhone.setError(null);
        layoutMatkhau.setError(null);
        layoutXacnhanmatkhau.setError(null);
        layoutGenderError.setError(null);
        layoutNgaySinh.setError(null);

        if (name.isEmpty()) {
            layoutHovaTen.setError("Vui lòng nhập họ và tên");
            editHovaTen.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }

        if (phone.isEmpty() || phone.replaceAll("\\D", "").length() < 10 || phone.replaceAll("\\D", "").length() > 11) {
            layoutPhone.setError("Số điện thoại không hợp lệ");
            editPhone.requestFocus();
            return;
        }


        if (dateOfBirth.isEmpty()) {
            layoutNgaySinh.setError("Vui lòng nhập ngày sinh");
            editNgaySinh.requestFocus();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  // Định dạng ngày sinh
            Date birthDate = sdf.parse(dateOfBirth);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate);

            // Lấy tuổi hiện tại
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int birthYear = birthCalendar.get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int birthMonth = birthCalendar.get(Calendar.MONTH);
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);

            int age = currentYear - birthYear;

            // Điều chỉnh nếu chưa đến sinh nhật trong năm nay
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }

            // Kiểm tra nếu tuổi nhỏ hơn 16
            if (age < 16) {
                layoutNgaySinh.setError("Bạn phải trên 16 tuổi");
                editNgaySinh.requestFocus();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            layoutNgaySinh.setError("Ngày sinh không hợp lệ");
            editNgaySinh.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            layoutMatkhau.setError("Vui lòng nhập mật khẩu");
            editMatkhau.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            layoutXacnhanmatkhau.setError("Mật khẩu xác nhận không khớp");
            editXacnhanmatkhau.requestFocus();
            return;
        }

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            layoutGenderError.setError("Vui lòng chọn giới tính");
            layoutGenderError.requestFocus();
            return;
        }

        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            layoutEmail.setError("Email đã được đăng ký với tài khoản khác");
                            editEmail.requestFocus();
                        } else {
                            String userId = UUID.randomUUID().toString();
                            User user = new User(userId, name, password, "user", phone, dateOfBirth, email,gender);
                            user.saveToFirestore();
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi kiểm tra email. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
