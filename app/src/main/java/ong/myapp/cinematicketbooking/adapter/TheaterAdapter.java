package ong.myapp.cinematicketbooking.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ong.myapp.cinematicketbooking.QuenMatKhauActivity;
import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.ShowDatesActivity;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {

    private List<String> theaterList;
    private String movieId; // Biến để lưu movieId đã chọn

    // Constructor cập nhật để nhận thêm movieId
    public TheaterAdapter(List<String> theaterList, String movieId) {
        this.theaterList = theaterList;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        String theaterId = theaterList.get(position);
        holder.textViewTheater.setText(theaterId);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ShowDatesActivity.class);
            intent.putExtra("movieId", movieId); // Truyền movieId đã chọn
            intent.putExtra("theaterId", theaterId); // Truyền theaterId đã chọn
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return theaterList.size();
    }

    public static class TheaterViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTheater;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTheater = itemView.findViewById(R.id.textViewTheater);
        }
    }
}
