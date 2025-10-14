package ong.myapp.cinematicketbooking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import ong.myapp.cinematicketbooking.adapter.ImageSliderAdapter;
import ong.myapp.cinematicketbooking.adapter.MovieAdapter;
import ong.myapp.cinematicketbooking.model.Movie;

public class FirstFragment extends Fragment {

    private RecyclerView verticalRecyclerView;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private ImageSliderAdapter imgAdapter;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private List<ImageSlider> imageSliders;
    private Timer mTimer;
    private FirebaseFirestore db;
    private static final String ARG_USER_ID = "userId";
    private String userId;

    public static FirstFragment newInstance(String userId) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }

        db = FirebaseFirestore.getInstance();

        // Slider ngang
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.circle_Indicator);

        imageSliders = new ArrayList<>();
        imgAdapter = new ImageSliderAdapter(getContext(), imageSliders);
        viewPager.setAdapter(imgAdapter);

        circleIndicator.setViewPager(viewPager);
        imgAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        getListPhotoFromFirestore(); // Lấy danh sách hình ảnh từ Firestore
        autoSlideImg();


        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int numColumns;
        if (screenWidth <= 1440) {
            numColumns = 2;
        } else {
            numColumns = 3;
        }


        verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numColumns);
        verticalRecyclerView.setLayoutManager(gridLayoutManager);

        movieList = new ArrayList<>();
        loadMoviesFromFirestore();
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageView searchButton = view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovies(query);
            } else {
                Toast.makeText(requireActivity(), "Vui lòng nhập tên phim để tìm kiếm!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void searchMovies(String query) {
        // Lọc danh sách phim từ cơ sở dữ liệu hoặc danh sách hiện tại
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredMovies.add(movie);
            }
        }

        // Cập nhật Adapter
        if (filteredMovies.isEmpty()) {
            Toast.makeText(requireActivity(), "Không tìm thấy phim phù hợp!", Toast.LENGTH_SHORT).show();
        } else {
            movieAdapter.setMovies(filteredMovies); // Giả sử movieAdapter là Adapter của bạn
            movieAdapter.notifyDataSetChanged();
        }
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
                            movieAdapter = new MovieAdapter(movieList, getContext(),userId);
                            verticalRecyclerView.setAdapter(movieAdapter);
                        } else {
                            Log.w("Firestore", "Error getting movies.", task.getException());
                        }
                    }
                });
    }
    //hàm này còn dang gắn cứng du liệu cần sửa lại
    private void getListPhotoFromFirestore() {
        db.collection("Movie")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            imageSliders.clear(); // Xóa danh sách cũ để tránh trùng lặp
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageUrl = document.getString("coverPhoto");
                                if (imageUrl != null) {
                                    imageSliders.add(new ImageSlider(imageUrl));
                                }
                            }
                            // Thông báo cho adapter rằng dữ liệu đã thay đổi
                            imgAdapter.notifyDataSetChanged();

                            // Gọi autoSlideImg() khi dữ liệu đã sẵn sàng
                            autoSlideImg();
                        } else {
                            Log.w("Firestore", "Lỗi khi lấy dữ liệu hình ảnh.", task.getException());
                        }
                    }
                });
    }


    private void autoSlideImg() {
        if (imageSliders == null || imageSliders.isEmpty() || viewPager == null) {
            return;
        }

        // Init Timer
        if (mTimer == null) {
            mTimer = new Timer();
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItem = imageSliders.size() - 1;
                        if (currentItem < totalItem) {
                            currentItem++;
                            viewPager.setCurrentItem(currentItem);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }, 5000, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}