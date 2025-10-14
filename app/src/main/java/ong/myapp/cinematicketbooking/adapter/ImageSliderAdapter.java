package ong.myapp.cinematicketbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import ong.myapp.cinematicketbooking.ImageSlider;
import ong.myapp.cinematicketbooking.R;

public class ImageSliderAdapter extends PagerAdapter {

    private Context mContext;
    private List<ImageSlider> mListImage;

    public ImageSliderAdapter(Context mContext, List<ImageSlider> mListImage) {
        this.mContext = mContext;
        this.mListImage = mListImage;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_image, container, false);
        ImageView img = view.findViewById(R.id.imageView);

        ImageSlider imgSli = mListImage.get(position);
        if (imgSli != null) {
            Glide.with(mContext).load(imgSli.getImageUrl()).into(img);
        }
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return (mListImage != null) ? mListImage.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
