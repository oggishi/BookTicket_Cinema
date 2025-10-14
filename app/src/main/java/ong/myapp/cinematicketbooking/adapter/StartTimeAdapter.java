package ong.myapp.cinematicketbooking.adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ong.myapp.cinematicketbooking.R;

public class StartTimeAdapter extends RecyclerView.Adapter<StartTimeAdapter.StartTimeViewHolder> {

    private List<String> startTimes;

    public StartTimeAdapter(List<String> startTimes) {
        this.startTimes = startTimes;
    }

    @NonNull
    @Override
    public StartTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_start_time, parent, false);
        return new StartTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartTimeViewHolder holder, int position) {
        String time = startTimes.get(position);
        holder.editTextTime.setText(time);

        // Thêm TextWatcher để lắng nghe sự thay đổi của EditText
        holder.editTextTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ghi nhận thay đổi vào danh sách startTimes
                startTimes.set(holder.getAdapterPosition(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi text thay đổi
            }
        });

        // Xử lý sự kiện khi nhấn nút xóa thời gian
        holder.btnDeleteTime.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            startTimes.remove(currentPosition);
            notifyItemRemoved(currentPosition);
            notifyItemRangeChanged(currentPosition, startTimes.size());
        });
    }

    @Override
    public int getItemCount() {
        return startTimes.size();
    }

    public List<String> getStartTimes() {
        return startTimes;
    }

    public static class StartTimeViewHolder extends RecyclerView.ViewHolder {

        EditText editTextTime;
        ImageButton btnDeleteTime;

        public StartTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextTime = itemView.findViewById(R.id.editTextTime);
            btnDeleteTime = itemView.findViewById(R.id.btnDeleteTime);
        }
    }
}



