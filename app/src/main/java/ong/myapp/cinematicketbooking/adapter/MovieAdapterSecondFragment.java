package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ong.myapp.cinematicketbooking.MovieInfor;
import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.adapter.ShowTimeAdapter;
import ong.myapp.cinematicketbooking.model.Show;
import ong.myapp.cinematicketbooking.model.Movie;


public class MovieAdapterSecondFragment extends RecyclerView.Adapter<MovieAdapterSecondFragment.MovieViewHolder> {

    private Context context;
    private List<Movie> movieArray;
    private List<Show> showArray;
    private String userId;

    public MovieAdapterSecondFragment(Context context, List<Movie> movieArray, List<Show> showArray, String userId) {
        this.context = context;
        this.movieArray = movieArray;
        this.showArray = showArray;
        this.userId=userId;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_movie, parent, false);
        return new MovieViewHolder(view);
    }


    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieArray.get(position);
        Show show = showArray.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.durationTextView.setText(movie.getDuration());
        holder.age.setText(movie.getAgeLimitation());
        holder.date.setText(movie.getYear());
        Glide.with(context)
                .load(movie.getCoverPhoto()) // Make sure this is a valid URL
                .into(holder.posterImageView);
        // Xử lý thời gian show
        List<String> timeList = show.getStartTime();
        if (timeList != null && !timeList.isEmpty()) {
            ShowTimeAdapter showTimeAdapter = new ShowTimeAdapter(
                    context,
                    timeList,
                    movie.getTitle(),
                    movie.getDuration(),
                    movie.getAgeLimitation(),
                    show.getShowId(),
                    movie.getMovieId(),
                    show.getSubType(),
                    show.getTheaterId(),
                    show.getDate(),
                    movie.getCoverPhoto(),
                    userId
            );
            Log.d("showId", show.getShowId());
            holder.recyclerViewTime.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)); // Set horizontal layout
            holder.recyclerViewTime.setAdapter(showTimeAdapter);
            holder.titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Khởi động MovieInfor activity khi nhấn vào poster
                    Intent intent = new Intent(context, MovieInfor.class);
                    intent.putExtra("movieId", movie.getMovieId());
                    context.startActivity(intent);
                }
            });
            holder.posterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieInfor.class);
                    intent.putExtra("movieId", movie.getMovieId());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.recyclerViewTime.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return showArray.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        Button titleTextView;
        TextView durationTextView;
        ImageButton posterImageView;
        TextView date;
        TextView age;
        RecyclerView recyclerViewTime;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.btnTitle);
            durationTextView = itemView.findViewById(R.id.duration);
            posterImageView = itemView.findViewById(R.id.poster);
            date = itemView.findViewById(R.id.streamingDate);
            age = itemView.findViewById(R.id.age_limit);
            recyclerViewTime = itemView.findViewById(R.id.recyclerViewTime);
        }
    }
}