package ong.myapp.cinematicketbooking.model;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

public class Show {
    public String showId;
    public String date;
    public List<String> startTime; // Danh sách các giờ chiếu trong ngày
    public String movieId;
    private String theaterId;
    private String subType;


    public Show() {
        // Default constructor required for calls to DataSnapshot.getValue(Show.class)
    }

    public Show(String showId, String date, List<String> startTime, String movieId, String theaterId,String subType) {
        this.showId = showId;
        this.date = date;
        this.startTime = startTime;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.subType=subType;
    }

    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference showRef = db.collection("Show").document(showId);
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

    // Getters and Setters
    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getStartTime() {
        return startTime;
    }


    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }
    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
