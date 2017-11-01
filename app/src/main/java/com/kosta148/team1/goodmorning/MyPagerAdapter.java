package com.kosta148.team1.goodmorning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Daehee on 2017-04-26.
 */

class MyPagerAdapter extends FragmentStatePagerAdapter{

    static Fragment fragments[] = new Fragment[2];
    String tabTitle[] = new String[]{"시간별 날씨", "실시간 SNS"};

    // Constructor
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new WeatherListFragment();
        fragments[1] = new WeatherTalkFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("getItem 호출", position + " 번째");

//        Fragment fr = null;
//
//        switch(position) {
//            case 0:
//                fr = new WeatherListFragment();
//                break;
//            case 1:
//                fr = new WeatherTalkFragment();
//                break;
//        }

//        Fragment fragment = fragments[position];
//        혹시 모를 값 전달에 대비
//        Bundle args = new Bundle();
//        args.putInt("", 0);
//        fragment.setArguments(args)
        return fragments[position];
//        return fr;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
} // end of class
