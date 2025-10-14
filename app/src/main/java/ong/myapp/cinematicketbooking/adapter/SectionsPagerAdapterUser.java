package ong.myapp.cinematicketbooking.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ong.myapp.cinematicketbooking.FirstFragment;
import ong.myapp.cinematicketbooking.ProfileFragment;
import ong.myapp.cinematicketbooking.SecondFragment;
import ong.myapp.cinematicketbooking.ThirdFragment;

public class SectionsPagerAdapterUser extends FragmentPagerAdapter {

    private String userId; // Thuộc tính để lưu userId

    public SectionsPagerAdapterUser(FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId; // Gán giá trị userId vào thuộc tính
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FirstFragment.newInstance(userId); // Truyền userId vào FirstFragment
            case 1:
                return SecondFragment.newInstance(userId); // Truyền userId vào SecondFragment
            case 2:
                return ProfileFragment.newInstance(userId); // Truyền userId vào ProfileFragment
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}