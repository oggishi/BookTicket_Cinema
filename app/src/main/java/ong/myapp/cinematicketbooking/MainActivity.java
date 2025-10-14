package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ong.myapp.cinematicketbooking.adapter.MovieAdapter;
import ong.myapp.cinematicketbooking.adapter.SectionsPagerAdapter;
import ong.myapp.cinematicketbooking.model.Movie;
import ong.myapp.cinematicketbooking.model.Show;
import ong.myapp.cinematicketbooking.model.User;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tabTitle;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        anhXa();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        User u = new User(
                "admin123",         // userId
                "Admin",        // name
                "1",     // password
                "admin",// role
                "0123456789",
                "01/01/1990",      // dateOfBirth
                "admin@gmail.com", // email
                "Nam"
        );

        u.saveToFirestore();
    }

    private void anhXa() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        tabLayout = findViewById(R.id.tabLayout);

    }


    private void setupTabIcons(TabLayout tabLayout) {
        // Tab đầu tiên
        View tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ImageView iconOne = tabOne.findViewById(R.id.icon);
        iconOne.setImageResource(R.drawable.home30);
        TextView titleOne = tabOne.findViewById(R.id.title);
        titleOne.setText("Trang chủ");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        // Tab thứ hai
        View tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ImageView iconTwo = tabTwo.findViewById(R.id.icon);
        iconTwo.setImageResource(R.drawable.cinema30);
        TextView titleTwo = tabTwo.findViewById(R.id.title);
        titleTwo.setText("Suất chiếu");
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        // Tab thứ ba
        View tabThree = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ImageView iconThree = tabThree.findViewById(R.id.icon);
        iconThree.setImageResource(R.drawable.account30);
        TextView titleThree = tabThree.findViewById(R.id.title);
        titleThree.setText("Tài khoản");
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }
}
