package ong.myapp.cinematicketbooking.model;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Seat {
    private  String seatId;

    private String showId;
    private String value;
    private String startTime;
    private String userId;
public Seat(){

}
    public Seat(String seatId,String value, String showId, String startTime, String userId) {
        this.seatId = seatId;
        this.value = value;
        this.showId = showId;
        this.startTime = startTime;
        this.userId = userId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference showRef = db.collection("Seat").document(seatId);
        showRef.set(this, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Show data successfully saved!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error saving show data", e);
                    }
                });
    }
}
