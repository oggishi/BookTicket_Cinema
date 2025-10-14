package ong.myapp.cinematicketbooking.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.TheaterListActivity;
import ong.myapp.cinematicketbooking.model.Movie;

public class MovieAdapterShow extends RecyclerView.Adapter<MovieAdapterShow.MovieViewHolder> {

    private List<Movie> movieList;

    public MovieAdapterShow(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_show, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewDescription.setText(movie.getDirector());
        holder.textDate.setText(movie.getYear());

        Glide.with(holder.itemView.getContext())
                .load(movie.getCoverPhoto())
                .into(holder.imageViewPoster);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TheaterListActivity.class);
            intent.putExtra("movieId", movie.getMovieId()); // Truyền movieId đến TheaterListActivity
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDescription,textDate;
        ImageView imageViewPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
             textDate = itemView.findViewById(R.id.textViewDate);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
        }
    }
}
