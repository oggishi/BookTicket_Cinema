package ong.myapp.cinematicketbooking.model;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

public class Ticket {
    // Fields
    private String ticketId;
    private List<String> listSeatId;
    private String userId;
    private String dateBook;
    private String showTime;
    private String date;
    private String movieId;

    private static int ticketCounter = 0; // Counter for generating ticket IDs

    // Constructor with parameters
    public Ticket(String ticketId, List<String> listSeatId, String userId, String dateBook, String showTime,String date, String movieId) {
        this.ticketId = ticketId;
        this.listSeatId = listSeatId;
        this.userId = userId;
        this.dateBook = dateBook;
        this.showTime = showTime;
        this.date = date;
        this.movieId = movieId;
    }

    // Default constructor
    public Ticket() {}
    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public List<String> getListSeatId() {
        return listSeatId;
    }

    public void setListSeatId(List<String> listSeatId) {
        this.listSeatId = listSeatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getDateBook() {
        return dateBook;
    }

    public void setDateBook(String dateBook) {
        this.dateBook = dateBook;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ticketRef = db.collection("Ticket").document(ticketId);
        ticketRef.set(this, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Ticket data successfully saved!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error saving ticket data", e);
                    }
                });
    }
}