package ong.myapp.cinematicketbooking;

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

public class AdminActivity extends AppCompatActivity {

    private SectionsPagerAdapterAdmin sectionsPagerAdapterAdmin;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        sectionsPagerAdapterAdmin = new SectionsPagerAdapterAdmin(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(sectionsPagerAdapterAdmin);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        // Tab đầu tiên
        View tabOne = LayoutInflater.from(this).inflate(R.layout.admin_tab, null);
        ImageView iconOne = tabOne.findViewById(R.id.icon);
        iconOne.setImageResource(R.drawable.ic_edit);
        TextView titleOne = tabOne.findViewById(R.id.title);
        titleOne.setText("Quản lý phim");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        // Tab thứ hai
        View tabTwo = LayoutInflater.from(this).inflate(R.layout.admin_tab, null);
        ImageView iconTwo = tabTwo.findViewById(R.id.icon);
        iconTwo.setImageResource(R.drawable.ic_movie);
        TextView titleTwo = tabTwo.findViewById(R.id.title);
        titleTwo.setText("Quản lý suất chiếu");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
}
