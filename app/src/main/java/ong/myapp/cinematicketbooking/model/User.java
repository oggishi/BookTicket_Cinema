package ong.myapp.cinematicketbooking.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.util.Log;

public class User {
    public String userId;
    public String name,password,role,gender;
    public String phone;
    public String dateOfBirth;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String name, String password, String role, String phone, String dateOfBirth, String email, String gender) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.gender = gender;
    }

    public String getEmail(){
        return email;
    }
    public String getUserId(){
        return userId;
    }// Getters and Setters


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .document(userId) // Tạo document với userId
                .set(this) // Lưu đối tượng User vào Firestore
                .addOnSuccessListener(aVoid -> {
                    // Lưu thành công
                    Log.d("Firestore", "User successfully added!");
                })
                .addOnFailureListener(e -> {
                    // Lưu thất bại
                    Log.w("Firestore", "Error adding user", e);
                });
    }
}
