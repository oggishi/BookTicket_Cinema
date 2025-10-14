package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ong.myapp.cinematicketbooking.R;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private Context context;
    private List<String> seatList;
    private String selectedStartTime;
    private Map<String, String> seatTimeMap;
    private TextView numberTicket, sum,selectedSeatTextView;
    private ArrayList<String> selectedSeats;

    public SeatAdapter(Context context,List<String> seatList, Map<String, String> seatTimeMap, String selectedStartTime, TextView numberTicket, TextView sum, TextView selectedSeatTextView) {
        this.context = context;
        this.seatList = seatList;
        this.selectedStartTime = selectedStartTime;
        this.seatTimeMap = seatTimeMap;
        this.numberTicket = numberTicket;
        this.sum = sum;
        this.selectedSeats = new ArrayList<>();
        this.selectedSeatTextView = selectedSeatTextView;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        String seatValue = seatList.get(position);
        holder.seatValueText.setText(seatValue);

        if (seatTimeMap.containsKey(seatValue) && isSameStartTime(seatValue)) {
            holder.seatCheckbox.setChecked(true);
            holder.seatCheckbox.setBackgroundResource(R.drawable.checkbox_soldout);
            holder.seatCheckbox.setEnabled(false);
        } else {
            holder.seatCheckbox.setChecked(false);
            holder.seatCheckbox.setBackgroundResource(R.drawable.uncheck_checkbox_ticket);
            holder.seatCheckbox.setEnabled(true);
            holder.seatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.seatCheckbox.setBackgroundResource(R.drawable.check_checkbox_ticket);
                        if (!selectedSeats.contains(seatValue)) {
                            selectedSeats.add(seatValue);
                        }
                    } else {
                        holder.seatCheckbox.setBackgroundResource(R.drawable.uncheck_checkbox_ticket);
                        selectedSeats.remove(seatValue);
                    }

                    numberTicket.setText(selectedSeats.size() + "x Ghế");
                    sum.setText(selectedSeats.size() * 50000 + "đ");

                    StringBuilder seatsBuilder = new StringBuilder();

                    for (int i = 0; i < selectedSeats.size(); i++) {
                        seatsBuilder.append(selectedSeats.get(i));
                        if (i < selectedSeats.size() - 1) {
                            seatsBuilder.append(", ");
                        }
                    }
                    selectedSeatTextView.setText(seatsBuilder.toString());
                }

            });

        }}


    private boolean isSameStartTime(String value) {
        // Lấy startTime của ghế từ map seatTimeMap
        String seatStartTime = seatTimeMap.get(value);
        return seatStartTime != null && seatStartTime.equals(selectedStartTime);
    }


    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public ArrayList<String> getSelectedSeats() {
        return new ArrayList<>(selectedSeats); // Trả về bản sao của danh sách ghế đã chọn
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        CheckBox seatCheckbox;
        TextView seatValueText;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatCheckbox = itemView.findViewById(R.id.seatCheckbox);
            seatValueText = itemView.findViewById(R.id.seatValueText);
        }
    }
}
