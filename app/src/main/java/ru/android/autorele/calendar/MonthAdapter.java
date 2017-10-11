package ru.android.autorele.calendar;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.android.autorele.R;
import ru.android.autorele.calendar.view.CalendarFragment;

/**
 * Created by yasina on 01.10.17.
 */

public class MonthAdapter extends FragmentPagerAdapter {

    public List<CalendarFragment> mFragmentList = new ArrayList<>();
    private Context mContext;
    private final String[] mFragmentTitleList;

    public MonthAdapter(FragmentManager manager, Context context) {
        super(manager);
        mContext = context;
        mFragmentTitleList = mContext.getResources().getStringArray(R.array.months);
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
