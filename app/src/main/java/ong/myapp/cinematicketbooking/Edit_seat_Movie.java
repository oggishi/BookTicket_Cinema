package ong.myapp.cinematicketbooking;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ong.myapp.cinematicketbooking.adapter.MovieAdapterShow;
import ong.myapp.cinematicketbooking.model.Movie;

public class Edit_seat_Movie extends Fragment {

    private RecyclerView recyclerViewMovies;
    private MovieAdapterShow movieAdapter;
    private FirebaseFirestore db;
    private List<Movie> movieList;

    public Edit_seat_Movie() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_seat__movie, container, false);

        // Initialize RecyclerView
        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firestore and list
        db = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapterShow(movieList);
        recyclerViewMovies.setAdapter(movieAdapter);

        // Load movies from Firestore
        loadMoviesFromFirestore();

        return view;
    }

    private void loadMoviesFromFirestore() {
        db.collection("Movie")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Movie movie = document.toObject(Movie.class);
                                movieList.add(movie);
                            }
                            movieAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle the error
                    }
                });
    }
}
