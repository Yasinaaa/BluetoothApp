package ru.android.bluetooth.calendar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yasina on 01.10.17.
 */

public class MonthAdapter extends FragmentPagerAdapter {

    public List<CalendarFragment> mFragmentList = new ArrayList<>();
    private final String[] mFragmentTitleList = new String[]{
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август","Сентябрь","Октябрь","Ноябрь","Декабрь"
    };

    public MonthAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public CalendarFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(CalendarFragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList[position];
    }
}
