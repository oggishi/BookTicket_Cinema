package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import ong.myapp.cinematicketbooking.DetailsTicket;
import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.model.Ticket;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> ticketList;
    private final Map<String, String> movieTitleMap;
    private final Map<String, String> moviePosterMap;
    private Context context;

    public TicketAdapter(Context context, List<Ticket> ticketList, Map<String, String> movieTitleMap, Map<String, String> moviePosterMap) {
        this.ticketList = ticketList;
        this.movieTitleMap = movieTitleMap;
        this.moviePosterMap = moviePosterMap;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Collections.reverse(ticketList);
        Ticket ticket = ticketList.get(position);
        String movieTitle = movieTitleMap.get(ticket.getMovieId());
        String moviePoster = moviePosterMap.get(ticket.getMovieId());

        // Định dạng ngày theo kiểu dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date today = new Date(); // Ngày hiện tại

        try {
            if (ticket.getDateBook() != null) { // Kiểm tra null để tránh NullPointerException
                Date ticketDate = dateFormat.parse(ticket.getDateBook());

                long diffInMillies = Math.abs(today.getTime() - ticketDate.getTime());
                long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

                if (diffInDays <= 90) {
                    holder.title.setText(movieTitle);
                    Glide.with(holder.itemView.getContext())
                            .load(moviePoster)
                            .error(R.drawable.joker) // Đặt ảnh lỗi tạm thời
                            .into(holder.poster);

                    holder.time.setText("Suất: " + ticket.getShowTime());
                    holder.dateBook.setText(ticket.getDateBook());
                    holder.date.setText(ticket.getDate());
                    holder.amount.setText(ticket.getListSeatId().size() * 50000 + "đ");
                    holder.quantity.setText("Số lượng ghế: " + ticket.getListSeatId().size());



                    holder.itemView.setVisibility(View.VISIBLE); // Hiển thị item
                } else {
                    // Ẩn item nếu không thỏa mãn điều kiện
                    holder.itemView.setVisibility(View.GONE);
                }
            } else {
                // Nếu dateBook là null, bạn có thể ẩn hoặc xử lý theo cách khác
                holder.itemView.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(holder.itemView.getContext(), "Lỗi định dạng ngày: " + ticket.getDateBook(), Toast.LENGTH_SHORT).show();
            holder.itemView.setVisibility(View.GONE); // Ẩn item khi lỗi parse
        }// Thiết lập click listener cho poster
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsTicket.class);

                // Truyền dữ liệu vào Intent (Cảnh báo: Kiểm tra null trước khi truyền)
                intent.putExtra("showTime", ticket.getShowTime() != null ? ticket.getShowTime() : "N/A");
                intent.putExtra("movieTitle", movieTitle != null ? movieTitle : "Không có tên phim");
                intent.putExtra("moviePoster", moviePoster != null ? moviePoster : "default_poster_url");
                intent.putExtra("dateBook", ticket.getDateBook() != null ? ticket.getDateBook() : "N/A");
                intent.putExtra("date", ticket.getDate() != null ? ticket.getDate() : "N/A");
                intent.putExtra("amount", ticket.getListSeatId().size() * 50000 + "đ");
                intent.putExtra("quantity", "Số lượng ghế: " + ticket.getListSeatId().size());
                intent.putExtra("ticketId", ticket.getTicketId() != null ? ticket.getTicketId() : "N/A");

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        private ImageButton poster;
        private TextView title, time, date, amount, quantity, dateBook;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            quantity = itemView.findViewById(R.id.quantity);
            dateBook = itemView.findViewById(R.id.dateBook);

        }
    }


}

