package ong.myapp.cinematicketbooking.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ong.myapp.cinematicketbooking.Edit_Movie;
import ong.myapp.cinematicketbooking.Edit_seat_Movie;

public class SectionsPagerAdapterAdmin extends FragmentPagerAdapter {

    public SectionsPagerAdapterAdmin(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Edit_Movie(); // Tab 1: Sửa phim
            case 1:
                return new Edit_seat_Movie(); // Tab 2: Sửa chỗ ngồi
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 2; // Tổng số tab
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Sửa phim";
            case 1:
                return "Sửa chỗ ngồi";
            default:
                return null;
        }
    }
}
