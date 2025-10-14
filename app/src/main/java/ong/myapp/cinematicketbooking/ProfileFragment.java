package ong.myapp.cinematicketbooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ong.myapp.cinematicketbooking.adapter.TicketAdapter;
import ong.myapp.cinematicketbooking.model.Movie;
import ong.myapp.cinematicketbooking.model.Ticket;
import ong.myapp.cinematicketbooking.model.User;

public class ProfileFragment extends Fragment {
    private List<Movie> movieList;
    private List<Ticket> ticketList;
    private List<Ticket> selectedTicket;
    private FirebaseFirestore ticketRef;
    private FirebaseFirestore movieRef;
    private RecyclerView recyclerView;
    private static final String ARG_USER_ID = "userId";
    private String userId;
    private ImageButton viewProfile;
    private Button logout;
    private TextView emailTextView, cusNameTextView;
    private ArrayList<User> userList;
    private FirebaseFirestore db;
    private boolean isTicketsLoaded;
    private boolean isMoviesLoaded;
    private String name, dateOfBirth, phone, email, userGender;

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        isTicketsLoaded = false;
        isMoviesLoaded = false;
        ticketRef = FirebaseFirestore.getInstance();
        movieRef = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        cusNameTextView = view.findViewById(R.id.cusName);
        logout = view.findViewById(R.id.logout);
        viewProfile= view.findViewById(R.id.viewProfile);
        emailTextView= view.findViewById(R.id.email);
        movieList = new ArrayList<>();
        ticketList = new ArrayList<>();
        selectedTicket = new ArrayList<>();

        loadMoviesFromFirestore();
        loadTicketsFromFirestore();

        logout.setOnClickListener(new View.OnClickListener() {
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

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();
        // Lấy thông tin User từ Firestore
        loadUsersFromFirestore();
        viewProfile.setOnClickListener(view1 -> {
            // Tạo Intent để chuyển sang DetailsProfileActivity
            Intent intent = new Intent(getActivity(), DetailsProfile.class);
            intent.putExtra("name", name);
            intent.putExtra("dateOfBirth", dateOfBirth);
            intent.putExtra("phone", phone);
            intent.putExtra("email", email);
            intent.putExtra("gender", userGender);
            startActivity(intent);
        });
        return view;
    }

    private void loadUsersFromFirestore() {
        db.collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear(); // Xóa danh sách cũ để tránh trùng lặp
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }

                        // Xử lý dữ liệu sau khi tải xong
                        for (User user : userList) {
                            if (user.getUserId().equals(userId)) {
                                emailTextView.setText(user.getEmail());
                                cusNameTextView.setText(user.getName());
                                name = user.getName();
                                email = user.getEmail();
                                userGender = user.getGender();
                                dateOfBirth = user.getDateOfBirth();
                                phone = user.getPhone();
                                break;
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error getting users.", task.getException());
                    }
                });
    }


    private void loadDataAndSetupRecyclerView() {
        selectedTicket.clear();

        // Lọc danh sách các ticket phù hợp với userId
        for (Ticket ticket : ticketList) {
            if (ticket.getUserId().equals(userId)) {
                selectedTicket.add(ticket);
            }
        }

        // Tạo map chỉ chứa movieId liên quan đến selectedTicket
        Map<String, String> filteredMovieTitleMap = new HashMap<>();
        Map<String, String> filteredMoviePosterMap = new HashMap<>();

        for (Ticket ticket : selectedTicket) {
            for (Movie m : movieList) {
                if (m.getMovieId().equals(ticket.getMovieId())) {
                    String movieTitle = m.getTitle();
                    String moviePoster = m.getCoverPhoto();

                    // Chỉ thêm vào Map nếu cả movieTitle và moviePoster đều không null
                    if (movieTitle != null && moviePoster != null) {
                        filteredMovieTitleMap.put(m.getMovieId(), movieTitle);
                        filteredMoviePosterMap.put(m.getMovieId(), moviePoster);
                    }
                }
            }
        }


        // Thiết lập RecyclerView với dữ liệu đã lọc
        setupRecyclerView(filteredMovieTitleMap, filteredMoviePosterMap);
    }

    private void setupRecyclerView(Map<String, String> filteredMovieTitleMap, Map<String, String> filteredMoviePosterMap) {
        LinearLayoutManager layoutManagerTicket = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManagerTicket);

        Collections.reverse(selectedTicket);
        // Khởi tạo adapter với Map đã lọc
        TicketAdapter ticketAdapter = new TicketAdapter(getContext(), selectedTicket, filteredMovieTitleMap, filteredMoviePosterMap);
        recyclerView.setAdapter(ticketAdapter);
    }

    private void loadTicketsFromFirestore() {
        ticketRef.collection("Ticket")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ticketList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ticket ticket = document.toObject(Ticket.class);
                                ticketList.add(ticket);
                            }
                            isTicketsLoaded = true;
                            checkAndSetupRecyclerView();

                        } else {
                            Log.w("Firestore", "Error getting tickets.", task.getException());
                        }
                    }
                });
    }

    private void loadMoviesFromFirestore() {
        movieRef.collection("Movie")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            movieList.clear(); // Clear old list to avoid duplicates
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie movie = document.toObject(Movie.class);
                                movieList.add(movie);
                            }
                            isMoviesLoaded = true;
                            checkAndSetupRecyclerView();
                        } else {
                            Log.w("Firestore", "Error getting tickets.", task.getException());
                        }
                    }
                });
    }


    private void checkAndSetupRecyclerView() {
        if (isTicketsLoaded && isMoviesLoaded) {
            loadDataAndSetupRecyclerView();
        }
    }
}
