package ong.myapp.cinematicketbooking.model;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Movie {
    private String movieId;
    private String title;
    private String year;
    private String nation;
    private String genres;
    private String language;
    private String ageLimitation;
    private String coverPhoto;
    private String duration;
    private String description;
    private String director;
    private String actors;

    public Movie(String movieId, String title, String year, String nation, String genres, String language, String ageLimitation, String coverPhoto, String duration, String description,  String director, String actors) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.nation = nation;
        this.genres = genres;
        this.language = language;
        this.ageLimitation = ageLimitation;
        this.coverPhoto = coverPhoto;
        this.duration = duration;
        this.description = description;
        this.director=director;
        this.actors=actors;
    }

    public Movie() {

    }


    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference movieRef = db.collection("Movie").document(movieId);
        movieRef.set(this, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Movie data successfully saved!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error saving movie data", e);
                    }
                });
    }

    public void deleteFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference movieRef = db.collection("Movie").document(movieId);
        movieRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Movie data successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error deleting movie data", e);
                    }
                });
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAgeLimitation() {
        return ageLimitation;
    }

    public void setAgeLimitation(String ageLimitation) {
        this.ageLimitation = ageLimitation;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

}
