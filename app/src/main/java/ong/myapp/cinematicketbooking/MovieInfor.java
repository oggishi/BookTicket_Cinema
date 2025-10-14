package ong.myapp.cinematicketbooking;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ong.myapp.cinematicketbooking.adapter.DayAdapter;
import ong.myapp.cinematicketbooking.adapter.ShowTimeAdapter;
import ong.myapp.cinematicketbooking.model.Movie;
import ong.myapp.cinematicketbooking.model.Show;

public class MovieInfor extends AppCompatActivity implements DayAdapter.OnDayClickListener{
    private TextView noShowsTextView, descriptionTextView, titleTextView, ageLimitTextView, subTypeTextView, durationTextView, streamingDateTextView, genresTextView, nationTextView, directorTextView, actorsTextView;
    private Movie movie;
    private List<Movie> movieList;
    private FirebaseFirestore movieRef;
    private FirebaseFirestore showRef;
    private ImageButton btnBack;
    private ImageView coverPhotoImageView;
    private RecyclerView recyclerShowTime, recyclerViewDay;
    private List<Show> showList;
    private Show show;
    private String todayDate,userId;
    private List<Show> filteredShows;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_infor);

        btnBack = findViewById(R.id.btnBack);
        coverPhotoImageView = findViewById(R.id.poster);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        movieRef = FirebaseFirestore.getInstance();
        showRef = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
        showList = new ArrayList<>();

        Intent intent = getIntent();
        String movieId = intent.getStringExtra("movieId");
        userId = intent.getStringExtra("userId");

        ageLimitTextView = findViewById(R.id.age_limit);
        descriptionTextView = findViewById(R.id.description);
        titleTextView = findViewById(R.id.title);
        durationTextView = findViewById(R.id.duration);
        streamingDateTextView = findViewById(R.id.streamingDate);
        genresTextView = findViewById(R.id.genres);
        nationTextView = findViewById(R.id.nation);
        directorTextView = findViewById(R.id.director_name);
        actorsTextView = findViewById(R.id.actors_name);
        recyclerShowTime = findViewById(R.id.recyclerShowTime);
        recyclerViewDay = findViewById(R.id.recyclerViewDay);
        noShowsTextView = findViewById(R.id.noShows);
        subTypeTextView = findViewById(R.id.subType);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        todayDate = dateFormat.format(calendar.getTime());
        LinearLayoutManager layoutManagerDay = new LinearLayoutManager(MovieInfor.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDay.setLayoutManager(layoutManagerDay);
        DayAdapter dayAdapter = new DayAdapter(MovieInfor.this, this); // Gọi interface `OnDayClickListener`
        recyclerViewDay.setAdapter(dayAdapter);
        loadMoviesFromFirestore(movieId);
        loadShowsFromFirestore(movieId);
        filteredShows = new ArrayList<>();

    }

    private void loadMoviesFromFirestore(String movieId) {
        movieRef.collection("Movie")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            movieList.clear(); // Clear old list to avoid duplicates
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie movie = document.toObject(Movie.class);
                                movieList.add(movie);
                            }

                            // Sau khi load xong, tìm movie theo movieId
                            movie = getMovieById(movieList, movieId);

                            // Kiểm tra nếu movie không null thì set dữ liệu
                            if (movie != null) {
                                ageLimitTextView.setText(movie.getAgeLimitation());
                                descriptionTextView.setText(movie.getDescription());
                                titleTextView.setText(movie.getTitle());
                                durationTextView.setText(movie.getDuration());
                                streamingDateTextView.setText(movie.getYear());
                                genresTextView.setText(movie.getGenres());
                                nationTextView.setText(movie.getNation());
                                directorTextView.setText(movie.getDirector());
                                actorsTextView.setText(movie.getActors());
                                String imageUrl = movie.getCoverPhoto();
                                Glide.with(MovieInfor.this)
                                        .load(imageUrl)
                                        .into(coverPhotoImageView);


                            } else {
                                Toast.makeText(MovieInfor.this, "Movie not found", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.w("Firestore", "Error getting movies.", task.getException());
                        }
                    }
                });
    }

    private Movie getMovieById(List<Movie> movieList, String movieId) {
        for (Movie movie : movieList) {
            if (movie.getMovieId().equals(movieId)) {
                return movie;
            }
        }
        return null;
    }

    private void loadShowsFromFirestore(String movieId) {
        showRef.collection("Show")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            showList.clear(); // Clear old list to avoid duplicates
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Show show1 = document.toObject(Show.class);
                                showList.add(show1);
                            }
                            filterShowsByDate(todayDate);
                        } else {
                            Log.w("Firestore", "Không có suất chiếu.", task.getException());
                        }
                    }
                });
    }

    private void filterShowsByDate(String selectedDay) {
        boolean hasShows = false; // Cờ kiểm tra có show nào không

        for (Show show : showList) {
            if (show.getDate() != null && show.getDate().equals(selectedDay)) {
                if(show.getMovieId().equalsIgnoreCase(movie.getMovieId())) {
                    List<String> timeList = show.getStartTime();

                    if (timeList != null && !timeList.isEmpty()) {
                        // Hiển thị các showtime
                        recyclerShowTime.setLayoutManager(new LinearLayoutManager(MovieInfor.this, LinearLayoutManager.HORIZONTAL, false));
                        ShowTimeAdapter showTimeAdapter = new ShowTimeAdapter(MovieInfor.this, timeList,movie.getTitle(),movie.getDuration(),
                                movie.getAgeLimitation(),show.getShowId(),movie.getMovieId(), show.getSubType(), show.getTheaterId(), show.getDate(), movie.getCoverPhoto(),userId
                        );
                        recyclerShowTime.setAdapter(showTimeAdapter);

                        hasShows = true; // Có ít nhất một show phù hợp
                        noShowsTextView.setVisibility(View.GONE); // Ẩn noShowsTextView vì đã tìm thấy show
                        subTypeTextView.setText(show.getSubType());
                        break; // Dừng vòng lặp sau khi tìm thấy show phù hợp
                    }
                }
            }
        }

        // Nếu không có show nào phù hợp
        if (!hasShows) {
            recyclerShowTime.setVisibility(View.GONE);
            noShowsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerShowTime.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onDayClick(String selectedDay) {
        String formattedDay = selectedDay.split("\n")[1]; // Extract date in "dd/MM/yyyy" format
        Log.d("SelectedDay", "Selected Day: " + formattedDay); // Check the selected day
        filterShowsByDate(formattedDay);

    }
}

