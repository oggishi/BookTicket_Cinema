package ong.myapp.cinematicketbooking;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ong.myapp.cinematicketbooking.adapter.MovieAdapter;
import ong.myapp.cinematicketbooking.adapter.MovieAdapterAdmin;
import ong.myapp.cinematicketbooking.model.Movie;

public class Edit_Movie extends Fragment implements MovieAdapterAdmin.OnListEmptyListener {

    private RecyclerView recyclerView;
    private MovieAdapterAdmin movieAdapterAdmin;
    private List<Movie> movieList;
    private Button buttonAdd, buttonDeleteAll, buttonDeleteSelected,btnLogout;
    private FirebaseFirestore db;
    private static final int PICK_CSV_FILE = 123;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit__movie, container, false);

        // Initialize RecyclerView and Buttons
        recyclerView = view.findViewById(R.id.recyclerView);
        buttonAdd = view.findViewById(R.id.btnAddMovie);
        buttonDeleteAll = view.findViewById(R.id.btnDeleteAll);
        buttonDeleteSelected = view.findViewById(R.id.btnDeleteSelected);
        btnLogout = view.findViewById(R.id.logout);


        // Setup MovieAdapter and RecyclerView
        db = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
        loadMoviesFromFirestore();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup button click listeners
        buttonAdd.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Thêm Phim")
                    .setMessage("Bạn muốn thêm phim bằng cách nào?")
                    .setPositiveButton("Thủ công", (dialog, which) -> {
                        addMovieManually();
                    })
                    .setNegativeButton("Thêm bằng tệp", (dialog, which) -> {
                        selectCsvFile();
                    })
                    .show();
        });

        buttonDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa Tất Cả Phim")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả phim không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        for (Movie movie : movieList) {
                            String movieId = movie.getMovieId();

                            // Truy vấn Firestore để lấy ngày chiếu của từng Movie
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

                                            // Nếu không có ngày chiếu, bỏ qua việc xóa
                                            if (dateList.isEmpty()) {
                                                return;
                                            }

                                            // Tìm ngày chiếu mới nhất
                                            String latestDateStr = dateList.get(0);
                                            for (String date : dateList) {
                                                try {
                                                    if (sdf.parse(date).after(sdf.parse(latestDateStr))) {
                                                        latestDateStr = date;
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
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
                                                    db.collection("Movie").document(movieId).delete()
                                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Xóa phim thành công: " + movie.getTitle()))
                                                            .addOnFailureListener(e -> Log.w("Firestore", "Lỗi khi xóa phim: " + movie.getTitle(), e));

                                                    // Xóa cục bộ
                                                    movieList.remove(movie);
                                                    movieAdapterAdmin.notifyDataSetChanged();
                                                } else {
                                                    // Nếu ngày chiếu nhỏ hơn hoặc bằng 90 ngày so với ngày hiện tại, không được phép xóa
                                                    Toast.makeText(v.getContext(), "Không thể xóa phim này vì chưa đủ 90 ngày!", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }

                        if (movieList.isEmpty()) {
                            onListEmpty();
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        buttonDeleteSelected.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa Phim Đã Chọn")
                    .setMessage("Bạn có chắc chắn muốn xóa phim đã chọn không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        List<Integer> selectedItems = movieAdapterAdmin.getSelectedItems();

                        for (int i = selectedItems.size() - 1; i >= 0; i--) {
                            int position = selectedItems.get(i);
                            Movie movieToDelete = movieList.get(position);
                            String movieId = movieToDelete.getMovieId();

                            // Truy vấn Firestore để lấy ngày chiếu của từng Movie
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

                                            // Nếu không có ngày chiếu, xóa phim bình thường
                                            if (dateList.isEmpty()) {
                                                // Xóa mục này khỏi Firestore
                                                db.collection("Movie").document(movieId).delete()
                                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Xóa phim thành công: " + movieToDelete.getTitle()))
                                                        .addOnFailureListener(e -> Log.w("Firestore", "Lỗi khi xóa phim: " + movieToDelete.getTitle(), e));

                                                // Xóa cục bộ
                                                movieList.remove(position);
                                                movieAdapterAdmin.notifyItemRemoved(position);
                                                movieAdapterAdmin.notifyItemRangeChanged(position, movieList.size());
                                                return;
                                            }

                                            // Tìm ngày chiếu mới nhất
                                            String latestDateStr = dateList.get(0);
                                            for (String date : dateList) {
                                                try {
                                                    if (sdf.parse(date).after(sdf.parse(latestDateStr))) {
                                                        latestDateStr = date;
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
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
                                                    db.collection("Movie").document(movieId).delete()
                                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Xóa phim thành công: " + movieToDelete.getTitle()))
                                                            .addOnFailureListener(e -> Log.w("Firestore", "Lỗi khi xóa phim: " + movieToDelete.getTitle(), e));

                                                    // Xóa cục bộ
                                                    movieList.remove(position);
                                                    movieAdapterAdmin.notifyItemRemoved(position);
                                                    movieAdapterAdmin.notifyItemRangeChanged(position, movieList.size());
                                                } else {
                                                    // Nếu ngày chiếu nhỏ hơn hoặc bằng 90 ngày so với ngày hiện tại, không được phép xóa
                                                    Toast.makeText(v.getContext(), "Không thể xóa phim này vì chưa đủ 90 ngày!", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }

                        // Cập nhật lại giao diện sau khi xóa các phim đã chọn
                        movieAdapterAdmin.clearSelectedItems();
                        movieAdapterAdmin.updateSelectedItems();
                        movieAdapterAdmin.notifyDataSetChanged();

                        if (movieList.isEmpty()) {
                            onListEmpty();
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            Toast.makeText(requireActivity(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                            // Chuyển hướng về màn hình đăng nhập
                            Intent intent = new Intent(requireActivity(), MainActivity.class);
                            startActivity(intent);
                            // Kết thúc Activity hiện tại
                            requireActivity().finish();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            // Đóng hộp thoại
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onListEmpty() {
        Toast.makeText(getContext(), "Danh sách phim hiện đang trống!", Toast.LENGTH_SHORT).show();
    }
    private void loadMoviesFromFirestore() {
        db.collection("Movie")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            movieList.clear(); // Xóa danh sách cũ để tránh trùng lặp
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie movie = document.toObject(Movie.class);
                                movieList.add(movie);
                            }
                            // Sử dụng MovieAdapter mới với context
                            movieAdapterAdmin = new MovieAdapterAdmin(movieList, getContext());
                            recyclerView.setAdapter(movieAdapterAdmin);
                        } else {
                            Log.w("Firestore", "Error getting movies.", task.getException());
                        }
                    }
                });
    }
    private void addMovieManually() {
        // Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String datePart = dateFormat.format(new Date());

        // Tạo query để lấy các ID bắt đầu bằng "movie_" và chứa ngày hiện tại
        db.collection("Movie")
                .orderBy("movieId", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("movieId", "movie_" + datePart + "000")
                .whereLessThanOrEqualTo("movieId", "movie_" + datePart + "999")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int nextNumber = 1; // Mặc định nếu chưa có movie nào hôm nay

                        // Nếu đã có movie với ngày hiện tại, tìm ID lớn nhất và tăng lên 1
                        if (!task.getResult().isEmpty()) {
                            String lastId = task.getResult().getDocuments().get(0).getId();
                            String lastNumberStr = lastId.substring(lastId.length() - 3); // Lấy 3 số cuối
                            nextNumber = Integer.parseInt(lastNumberStr) + 1;
                        }
                        // Định dạng số mới thành 3 chữ số, ví dụ: "002"
                        String newNumberStr = String.format("%03d", nextNumber);
                        String movieId = "movie_" + datePart + newNumberStr;

                        int position = movieList.size();

                        Movie newMovie = new Movie(
                                movieId,
                                "Tên phim",
                                "Ngày công chiếu",
                                "Quốc gia",
                                "Thể loại",
                                "Ngôn ngữ",
                                "18+",
                                "Link ảnh poster",
                                "Thời lượng",
                                "Mô tả phim",
                                "Đạo diễn",
                                "Diễn viên"
                        );
                        newMovie.saveToFirestore();
                        movieList.add(newMovie);
                        movieAdapterAdmin.addNewItem();
                        movieAdapterAdmin.notifyItemInserted(position);
                        recyclerView.scrollToPosition(position);
                    } else {
                        // Xử lý lỗi nếu không lấy được danh sách ID
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void selectCsvFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn tệp CSV"), PICK_CSV_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri csvUri = data.getData();
            if (csvUri != null) {
                // Thực hiện xử lý file CSV tại đây
                Toast.makeText(getContext(), "Tệp CSV đã được chọn: " + csvUri.getPath(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
