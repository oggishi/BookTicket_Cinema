package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ong.myapp.cinematicketbooking.MovieInfor;
import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private String userId;

    public MovieAdapter(List<Movie> movieList, Context context, String userId) {
        this.movieList = movieList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.age_limit.setText(movie.getAgeLimitation());
        // Sử dụng Glide để tải ảnh từ URL
        Log.d("GlideImageURL", "URL: " + movie.getCoverPhoto());
        Glide.with(holder.itemView.getContext())
                .load(movie.getCoverPhoto())
                .into(holder.moviePoster);
        holder.movieTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi động MovieInfor activity khi nhấn vào poster
                Intent intent = new Intent(context, MovieInfor.class);
                intent.putExtra("movieId", movie.getMovieId());
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieInfor.class);
                intent.putExtra("movieId", movie.getMovieId());
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView  age_limit;
        ImageView moviePoster;
        Button movieTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            age_limit = itemView.findViewById(R.id.age_limit);
            moviePoster = itemView.findViewById(R.id.moviePoster);
        }
    }

    public void setMovies(List<Movie> movies) {
        this.movieList = movies; // Giả sử bạn có biến movieList là danh sách phim gốc
        notifyDataSetChanged();  // Làm mới RecyclerView
    }

}
