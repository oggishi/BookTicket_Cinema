package ong.myapp.cinematicketbooking.adapter;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ong.myapp.cinematicketbooking.R;
import ong.myapp.cinematicketbooking.model.Movie;

public class MovieAdapterAdmin extends RecyclerView.Adapter<MovieAdapterAdmin.MovieViewHolder> {
    private List<Movie> movieList;
    private Context context;
    private OnListEmptyListener onListEmptyListener;
    private List<Boolean> selectedItems;

    public MovieAdapterAdmin(List<Movie> movieList, Context context, OnListEmptyListener onListEmptyListener) {
        this.movieList = movieList;
        this.context = context;
        this.onListEmptyListener = onListEmptyListener;
        this.selectedItems = new ArrayList<>(movieList.size());
        for (int i = 0; i < movieList.size(); i++) {
            selectedItems.add(false);
        }
    }

    public MovieAdapterAdmin(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
        this.selectedItems = new ArrayList<>(movieList.size());
        for (int i = 0; i < movieList.size(); i++) {
            selectedItems.add(false);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (movieList.isEmpty()) {
            Log.d("MovieAdapterAdmin", "Danh sách phim trống!");
        }
        else{
            Movie movie = movieList.get(position);
            holder.tvTitle.setText(movie.getTitle());
            holder.tvGenre.setText(movie.getGenres());
            holder.tvDuration.setText(movie.getDuration());
            holder.tvStartDate.setText(movie.getYear());

            Glide.with(holder.imgMovie.getContext())
                    .load(movie.getCoverPhoto())
                    .into(holder.imgMovie);

            holder.checkboxMovie.setChecked(selectedItems.get(position));

            holder.checkboxMovie.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (position < selectedItems.size()) {
                    selectedItems.set(position, isChecked);
                }
            });

            holder.btnEdit.setOnClickListener(v -> showEditDialog(position));
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xóa Phim Đã Chọn")
                        .setMessage("Bạn có chắc chắn muốn xóa phim không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Lấy ra ID của bộ phim cần xóa
                            String movieId = movieList.get(position).getMovieId();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Truy vấn Firestore để lấy ra các ngày của Show liên quan đến Movie
                            db.collection("Show")
                                    .whereEqualTo("movieId", movieId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            List<String> dateList = new ArrayList<>();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                                String date = document.getString("date");
                                                if (date != null) {
                                                    dateList.add(date);
                                                }
                                            }

                                            // Nếu không có ngày chiếu nào, không thực hiện xóa
                                            if (dateList.isEmpty()) {
                                                movie.deleteFromFirestore(); // Xóa từ Firestore
                                                movieList.remove(position);
                                                selectedItems.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, movieList.size());

                                                if (movieList.isEmpty() && onListEmptyListener != null) {
                                                    onListEmptyListener.onListEmpty();
                                                }
                                                updateSelectedItems();
                                                return;
                                            }

                                            // Tìm ngày chiếu mới nhất trong danh sách
                                            String latestDateStr = dateList.get(0);
                                            for (String date : dateList) {
                                                try {
                                                    if (sdf.parse(date).after(sdf.parse(latestDateStr))) {
                                                        latestDateStr = date;
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "Lỗi khi phân tích ngày!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }

                                            try {
                                                // Ngày hiện tại
                                                Calendar currentDate = Calendar.getInstance();

                                                // Ngày chiếu mới nhất
                                                Calendar latestShowDate = Calendar.getInstance();
                                                latestShowDate.setTime(sdf.parse(latestDateStr));

                                                // Tính khoảng cách ngày giữa ngày hiện tại và ngày chiếu mới nhất
                                                long differenceInMillis = currentDate.getTimeInMillis() - latestShowDate.getTimeInMillis();
                                                long daysDifference = differenceInMillis / (1000 * 60 * 60 * 24);

                                                if (daysDifference > 90) {
                                                    // Xóa mục này khỏi Firestore nếu ngày chiếu lớn hơn 90 ngày trước ngày hiện tại
                                                    movie.deleteFromFirestore(); // Xóa từ Firestore
                                                    movieList.remove(position);
                                                    selectedItems.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, movieList.size());

                                                    if (movieList.isEmpty() && onListEmptyListener != null) {
                                                        onListEmptyListener.onListEmpty();
                                                    }
                                                    updateSelectedItems();
                                                } else {
                                                    // Nếu ngày chiếu nhỏ hơn hoặc bằng 90 ngày so với ngày hiện tại, không được phép xóa
                                                    Toast.makeText(v.getContext(), "Không thể xóa phim này vì chưa đủ 90 ngày!", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                Toast.makeText(context, "Lỗi khi phân tích ngày!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "Lỗi khi tải dữ liệu từ Firestore!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                        .show();
            });

        }

    }

    private void showEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa Thông Tin Phim");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_movie, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etNation = dialogView.findViewById(R.id.etNation);
        EditText etLanguage = dialogView.findViewById(R.id.etLanguage);
        EditText etCoverPhoto = dialogView.findViewById(R.id.etCoverPhoto);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDirector = dialogView.findViewById(R.id.etDirector);
        EditText etActor = dialogView.findViewById(R.id.etActor);

        TextView tvAgeLimitation = dialogView.findViewById(R.id.etAgeLimitation);
        SeekBar seekBarAgeLimitation = dialogView.findViewById(R.id.seekBarAgeLimitation);

        Movie movie = movieList.get(position);
        etTitle.setText(movie.getTitle());
        etYear.setText(movie.getYear());
        etNation.setText(movie.getNation());
        etLanguage.setText(movie.getLanguage());
        etCoverPhoto.setText(movie.getCoverPhoto());
        etDuration.setText(movie.getDuration());
        etDescription.setText(movie.getDescription());
        etDirector.setText(movie.getDirector());
        etActor.setText(movie.getActors());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.genre_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);
        int genrePosition = adapter.getPosition(movie.getGenres());
        spinnerGenre.setSelection(genrePosition);

        int ageLimitation = Integer.parseInt(movie.getAgeLimitation().replace("+", ""));
        seekBarAgeLimitation.setProgress(ageLimitation);
        tvAgeLimitation.setText("Giới Hạn Tuổi: " + ageLimitation + "+");

        seekBarAgeLimitation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAgeLimitation.setText("Giới Hạn Tuổi: " + progress + "+");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý nếu không cần
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý nếu không cần
            }
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newTitle = etTitle.getText().toString();
            String newGenre = spinnerGenre.getSelectedItem().toString();
            String newYear = etYear.getText().toString();
            String newNation = etNation.getText().toString();
            String newLanguage = etLanguage.getText().toString();
            String newCoverPhoto = etCoverPhoto.getText().toString();
            String newDuration = etDuration.getText().toString();
            String newDescription = etDescription.getText().toString();
            String newDirector = etDirector.getText().toString();
            String newActor = etActor.getText().toString();
            String newAgeLimitation = seekBarAgeLimitation.getProgress() + "+";

            if (!newTitle.isEmpty() && !newGenre.isEmpty() && !newYear.isEmpty()) {
                movie.setTitle(newTitle);
                movie.setGenres(newGenre);
                movie.setYear(newYear);
                movie.setNation(newNation);
                movie.setLanguage(newLanguage);
                movie.setAgeLimitation(newAgeLimitation);
                movie.setCoverPhoto(newCoverPhoto);
                movie.setDuration(newDuration);
                movie.setDescription(newDescription);
                movie.setDirector(newDirector);
                movie.setActors(newActor);
                movie.saveToFirestore();
                notifyItemChanged(position);
                Toast.makeText(context, "Cập nhật phim thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvGenre, tvStartDate,tvDuration;
        ImageView imgMovie;
        ImageButton btnEdit, btnDelete;
        CheckBox checkboxMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            checkboxMovie = itemView.findViewById(R.id.checkboxMovie);
        }
    }

    public interface OnListEmptyListener {
        void onListEmpty();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selectedItemsList = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.get(i)) {
                selectedItemsList.add(i);
            }
        }
        return selectedItemsList;
    }

    public void clearSelectedItems() {
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItems.set(i, false);
        }
        updateSelectedItems();
    }

    public void addNewItem() {
        selectedItems.add(false);
    }

    public void updateSelectedItems() {
        selectedItems.clear();
        for (int i = 0; i < movieList.size(); i++) {
            selectedItems.add(false);
        }
    }
}
