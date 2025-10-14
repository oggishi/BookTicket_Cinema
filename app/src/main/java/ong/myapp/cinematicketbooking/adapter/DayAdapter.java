package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ong.myapp.cinematicketbooking.R;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private Context context;
    private ArrayList<String> dayArray;
    private OnDayClickListener onDayClickListener; // Interface cho sự kiện click

    public DayAdapter(Context context, OnDayClickListener onDayClickListener) {
        this.context = context;
        this.dayArray = generateDays();
        this.onDayClickListener = onDayClickListener; // Khởi tạo interface
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        // Bind the data for each day
        String dayInfo = dayArray.get(position);
        holder.btnDay.setText(dayInfo);
        holder.btnDay.setOnClickListener(v -> {
            if (onDayClickListener != null) {
                onDayClickListener.onDayClick(dayInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayArray.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        Button btnDay;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDay = itemView.findViewById(R.id.btnDay);
        }
    }

    public interface OnDayClickListener {
        void onDayClick(String selectedDay);
    }

    // Generate days for the current week starting from today
    private ArrayList<String> generateDays() {
        ArrayList<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));

        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            String dayOfWeek = dayOfWeekFormat.format(calendar.getTime());

            if (i == 0) {
                days.add("Hôm nay\n" + date); // For today
            } else {
                days.add(dayOfWeek + "\n" + date);
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day
        }

        return days;
    }
}
