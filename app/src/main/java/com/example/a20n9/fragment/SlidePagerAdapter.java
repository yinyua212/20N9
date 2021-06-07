package com.example.a20n9.fragment;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SlidePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> viewContainer;
    String[] tabTitles = new String[]{"Calendar", "Home", "Memory"};

    public SlidePagerAdapter(FragmentManager fm, List<Fragment> viewContainer) {
        super(fm);
        this.viewContainer = viewContainer;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return viewContainer.get(position);
    }

    @Override
    public int getCount() {
        return viewContainer.size();
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    //滑動切換的時候銷燬當前的元件
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
//        if (viewContainer.get(position).getView() != null)
//        ((ViewPager) container).removeView(viewContainer.get(position).getView());

    }
    //將當前檢視新增到container中並返回當前View檢視
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        if (viewContainer.get(position).getView() != null) {
//
//
//        ((ViewPager) container).addView(viewContainer.get(position).getView());
//        }
//        return viewContainer.get(position);
//    }
//    @Override
//    public boolean isViewFromObject(View arg0, Object arg1) {
//        return arg0 == arg1;
//    }
}
