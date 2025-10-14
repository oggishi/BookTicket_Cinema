package ong.myapp.cinematicketbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ong.myapp.cinematicketbooking.adapter.DayAdapter;
import ong.myapp.cinematicketbooking.model.Movie;
import ong.myapp.cinematicketbooking.model.Show;
import ong.myapp.cinematicketbooking.adapter.MovieAdapterSecondFragment;
public class SecondFragment extends Fragment implements DayAdapter.OnDayClickListener {

    private RecyclerView recyclerViewDay;
    private RecyclerView recyclerViewMovie;
    private TextView noShowsTextView; // TextView hiển thị thông báo khi không có suất chiếu
    private FirebaseFirestore db;
    private List<Movie> movieList;
    private List<Show> showList;
    private MovieAdapterSecondFragment movieAdapter;
    private String todayDate; // Biến để lưu ngày hiện tại
    private static final String ARG_USER_ID = "userId";
    private String userId;

    public static SecondFragment newInstance(String userId) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        db = FirebaseFirestore.getInstance();

        recyclerViewDay = view.findViewById(R.id.recyclerViewDay);
        recyclerViewMovie = view.findViewById(R.id.recyclerViewMovie);
        noShowsTextView = view.findViewById(R.id.noShowsTextView);

        // Setting up the horizontal RecyclerView for days
        LinearLayoutManager layoutManagerDay = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDay.setLayoutManager(layoutManagerDay);
        DayAdapter dayAdapter = new DayAdapter(getContext(), this); // Gọi interface `OnDayClickListener`
        recyclerViewDay.setAdapter(dayAdapter);

        // Setting up the vertical RecyclerView for movies
        LinearLayoutManager layoutManagerMovie = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewMovie.setLayoutManager(layoutManagerMovie);

        movieList = new ArrayList<>();
        showList = new ArrayList<>();

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        todayDate = dateFormat.format(calendar.getTime());

        // Load data from Firestore
        loadMoviesFromFirestore();


        return view;
    }


    private void loadMoviesFromFirestore() {
        db.collection("Movie")
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
                            loadShowsFromFirestore(); // Load shows after loading movies
                        } else {
                            Log.w("Firestore", "Error getting movies.", task.getException());
                        }
                    }
                });
    }

    private void loadShowsFromFirestore() {
        db.collection("Show")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            showList.clear(); // Clear old list to avoid duplicates
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Show show = document.toObject(Show.class);
                                showList.add(show);
                            }
                            filterShowsByDate(todayDate);
                        } else {
                            Log.w("Firestore", "Error getting shows.", task.getException());
                        }
                    }
                });
    }




    private void filterShowsByDate(String selectedDay) {
        List<Show> filteredShows = new ArrayList<>();
        List<Movie> filteredMovies = new ArrayList<>();

        for (Show show : showList) {
            if (show.getDate() != null && show.getDate().equals(selectedDay)) {
                filteredShows.add(show);

                for (Movie movie : movieList) {
                    if (movie.getMovieId() != null && movie.getMovieId().equals(show.getMovieId())) {
                        if (!filteredMovies.contains(movie)) {
                            filteredMovies.add(movie);
                        }
                        break;
                    }
                }
            }
        }

        if (filteredShows.isEmpty()) {
            recyclerViewMovie.setVisibility(View.GONE);
            noShowsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMovie.setVisibility(View.VISIBLE);
            noShowsTextView.setVisibility(View.GONE);

            updateMovieAdapter(filteredMovies, filteredShows);
        }
    }

    private void updateMovieAdapter(List<Movie> filteredMovieList, List<Show> filteredShowList) {
        if (filteredMovieList != null && !filteredMovieList.isEmpty() && filteredShowList != null && !filteredShowList.isEmpty()) {
            movieAdapter = new MovieAdapterSecondFragment(getContext(), filteredMovieList, filteredShowList,userId);
            recyclerViewMovie.setAdapter(movieAdapter);

        } else {

        }
    }




    @Override
    public void onDayClick(String selectedDay) {
        String formattedDay = selectedDay.split("\n")[1]; // Extract date in "dd/MM/yyyy" format
        Log.d("SelectedDay", "Selected Day: " + formattedDay); // Check the selected day
        filterShowsByDate(formattedDay);
    }

}
