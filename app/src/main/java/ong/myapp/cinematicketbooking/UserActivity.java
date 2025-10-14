package ong.myapp.cinematicketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ong.myapp.cinematicketbooking.adapter.SectionsPagerAdapterAdmin;
import ong.myapp.cinematicketbooking.adapter.SectionsPagerAdapterUser;

public class UserActivity extends AppCompatActivity {

    private SectionsPagerAdapterUser sectionsPagerAdapterUser;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        sectionsPagerAdapterUser = new SectionsPagerAdapterUser(getSupportFragmentManager(), userId);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(sectionsPagerAdapterUser);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }

    private void setupTabIcons() {
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
