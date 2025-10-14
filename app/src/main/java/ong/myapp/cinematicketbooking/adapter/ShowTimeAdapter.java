package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ong.myapp.cinematicketbooking.BookTicket;
import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.ProfileFragment;
import ong.myapp.cinematicketbooking.loginActivity;
import ong.myapp.cinematicketbooking.registerActivity;

public class ShowTimeAdapter extends RecyclerView.Adapter<ShowTimeAdapter.ShowTimeViewHolder> {
    private List<String> showTimeList;
    private Context context;
    private String title, duration, ageLimit, showId, movieId,subType,theaterId, date,coverPhoto,userId; // Add showId to the adapter

    // Constructor with showId parameter
    public ShowTimeAdapter(Context context, List<String> showTimeList, String title,
                           String duration, String ageLimit, String showId, String movieId,
                           String subType, String theaterId, String date, String coverPhoto, String userId) {
        this.context = context;
        this.showTimeList = showTimeList;
        this.title = title;
        this.duration = duration;
        this.ageLimit = ageLimit;
        this.showId = showId; // Assign showId
        this.movieId = movieId;
        this.subType =subType;
        this.theaterId = theaterId;
        this.date = date;
        this.coverPhoto = coverPhoto;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ShowTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowTimeViewHolder holder, int position) {
        String showTime = showTimeList.get(position);
        holder.showTimeButton.setText(showTime);

        holder.showTimeButton.setOnClickListener(view -> {
            if(userId != null){
                Intent intent = new Intent(context, BookTicket.class);
                intent.putExtra("time", showTime);
                intent.putExtra("movieId", movieId);
                intent.putExtra("title", title);
                intent.putExtra("userId", userId);
                intent.putExtra("duration", duration);
                intent.putExtra("ageLimit", ageLimit);
                intent.putExtra("showId", showId);
                intent.putExtra("subType", subType);
                intent.putExtra("theaterId", theaterId);
                intent.putExtra("date", date);
                intent.putExtra("coverPhoto", coverPhoto);
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, loginActivity.class);
                Toast.makeText(context, "Bạn phải đăng nhập để đặt vé!", Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showTimeList.size();
    }

    public void updateShowList(List<String> newShowTimes) {
        this.showTimeList.clear();
        this.showTimeList.addAll(newShowTimes);
        notifyDataSetChanged();
    }

    public static class ShowTimeViewHolder extends RecyclerView.ViewHolder {
        Button showTimeButton;

        public ShowTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            showTimeButton = itemView.findViewById(R.id.btnShowTime);
        }
    }
}
